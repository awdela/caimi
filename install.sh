#!/bin/sh
USAGE="\nUsage: `basename $0` [-h] [-host address] [-o operater] [-d debugPackageName] \n
\t -h: 命令行帮助信息 \n
\t -host: 远程操作的服务器的IP \n
\t -o: 远程操作指令 log | remote | replace \n
\t -debug: 指定远程服务器包名日志级别为 debug \n\n
\t 示例：\n
\t \t 1、一键编译 coordinator 并替换指定 ip 的 coordinator 并重新启动打开启动日志\n
\t \t install.sh -host 192.168.9.163 -o replace\n
\t \t 2、一键预览 coordinator.log 日志\n
\t \t install.sh -host 192.168.9.163 -o log\n
\t \t 3、一键打开指定服务器 coordinator 为远程调试模式，本地可预览日志\n
\t \t install.sh -p coordinator -host 192.168.9.163 -o remote\n
\t \t 5、一键指定包名为 debug 模式 \n
\t \t install.sh -p debug -host 192.168.9.163 -d com.lanysec.service.event.jointanalysis\n\n
\t 注意 -host 是必须参数\n
"


# 远程服务器的 ssh 账户密码
user="root"
password="lanyun123"
# 本地mvn
mvn_home="/cygdrive/d/apache-maven-3.6.0-bin/apache-maven-3.6.0/bin/mvn"
# 本地编译 coordinator 目录
coordinator_from_path="/cygdrive/e/WorkSpace/caimi/caimi-manager/caimi-coordinator/target/"
# 本地编译后生成的 coordinator jar 包的名称
coordinator_from_file="caimi-coordinator-1.0.0.jar"
# 远程替换的 coordinator 的 jar 包的名称
coordinator_del_file="caimi-coordinator.jar"
# 远程替换的 coordinator 的 jar 包的路径
coordinator_to_path="/usr/caimi/lib/"
log_path="/var/log/caimi/"

MM() {
    checker
	coordinator
}

# 检查输入参数
checker() {
  if [[ ! -n "$host" ]]; then
    echo -e ${USAGE}
    exit 0;
  fi
  # 如果没有指定操作命令 默认是替换
  if [[ ! -n "$operater" ]]; then
    operater="replace"
  fi
  DOUSAGE="\nDO Usage: `basename $0` 开始以下操作 \n
  \t 主机: '$host' \n
  \t 进行 '$operater' 操作\n
  \t 调试 '$debugPackageName' 操作\n
  "
  echo -e $DOUSAGE
}

# 启动 debug 日志
debug() {
  if [[ -n "$debugPackageName" ]]; then
    curl -X POST -H "Content-Type: text/plain" -d 'DEBUG' http://$host:10080/api/v1/log/$3
  fi
}


# coordinator 操作
coordinator() {
  if [[ -n "$operater" ]] && [[ $operater = 'log' ]]; then

      $sshpass_home/sshpass.exe -p $password ssh -o StrictHostKeyChecking=no -tt $user@$host tail -n 500 -f $log_path"/caimi-web.log"
      echo 'done !';
      exit 0;

  elif [[ -n "$operater" ]] && [[ $operater = 'remote' ]]; then

      # 首先停掉定时任务; 其次停掉 coordinator; 远程启动 coordinator
      $sshpass_home/sshpass.exe -p $password ssh -o StrictHostKeyChecking=no -tt $user@$host << EEOOFF
      service iptables stop
      service crond stop
      nohup java -Xmx2g -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -javaagent:../spring-instrument-4.3.7.RELEASE.jar -jar caimi-coordinator.jar
EEOOFF
      exit 0;

  elif [[ -n "$operater" ]] && [[ $operater = 'replace' ]]; then

      cd /cygdrive/e/WorkSpace/caimi/caimi-parent
      $mvn_home clean install
      cd /cygdrive/e/WorkSpace/caimi/caimi-conf
      $mvn_home clean install
      cd /cygdrive/e/WorkSpace/caimi/caimi-common
      $mvn_home clean install
      cd /cygdrive/e/WorkSpace/caimi/caimi-interfaces
      $mvn_home clean install
	  cd /cygdrive/e/WorkSpace/caimi/caimi-manager/caimi-services
      $mvn_home clean install -Dmaven.test.skip=true
      cd /cygdrive/e/WorkSpace/caimi/caimi-elasticsearch
      $mvn_home clean install -Dmaven.test.skip=true 
      cd /cygdrive/e/WorkSpace/caimi/caimi-manager
      $mvn_home clean install -Dmaven.test.skip=true
      if [ $host == 'localhost' ] || [ $host == '127.0.0.1' ]; then
          echo '本地编译 coordinator 已完成。'
          exit 0;
      fi
      # 先删除 coordinator.jar
	  cd $coordinator_from_path
	  mv -f $coordinator_from_file $coordinator_del_file
      echo '删除 '$host' '$coordinator_del_file
      sshpass -p $password ssh -o StrictHostKeyChecking=no -tt $user@$host rm -rf $coordinator_to_path$coordinator_del_file

      # 上传jar
      echo '上传 '$coordinator_del_file ' 到 ' $host
      sshpass -p $password scp -o StrictHostKeyChecking=no $coordinator_from_path$coordinator_del_file $user@$host:$coordinator_to_path

      # 执行更新
      sshpass -p $password ssh -o StrictHostKeyChecking=no -tt $user@$host > /dev/null 2>&1 << EEOOFF
      cd $coordinator_to_path
	  nohup java -Xmx1g -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -javaagent:spring-instrument-4.3.7.RELEASE.jar -jar caimi-coordinator.jar &
      exit
EEOOFF
      echo 'done !';
      # 查看 log
      sshpass -p $password ssh -o StrictHostKeyChecking=no -tt $user@$host tail -f $log_path"caimi-web.log"

  else
      echo " 参数 -o 输入有误，必须是 log、remote、replace 其中一项，默认是 replace \n"
      exit 0;
  fi
}

until [ $# -eq 0 ]
do
  case "$1" in
        -h | --help)
            echo -e ${USAGE}
            exit 0
            ;;
		-host | --address)
            shift
            host=$1
            shift
            ;;
        -o | --operation)
            shift
            operater=$1
            shift
            # break
            ;;
        -d | --debugPackageName)
            shift
            debugPackageName=$1
            shift
            ;;
        *)
            echo -e ${USAGE} >&2
            exit 1
            ;;
  esac
done

MM

