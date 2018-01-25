package caimi.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import caimi.web.beans.User;

@Mapper
public interface UserMapper {

	/**
	 * 只实现用户crud操作，暂且不对权限做区分
	 */
	User loadUserByUserNmae(@Param("username") String username);
	
	int updateUserEmail(@Param("email") String email, @Param("id") Long id);
	
	int updateUserEnabled(@Param("enabled") Boolean enabled, @Param("uid") Long uid);
	
	int deleteUserById(long uid);
	
	User getUserById(@Param("id") Long id);
	
	long reg(User user);
	
//	List<User> getUserByNickname(@Param("nickname") String nickname);
	
//	int deleteUserRolesByUid(Long id);
	
//	int setUserRoles(@Param("rids") Long[] rids, @Param("id") Long id);
	
}
