package caimi.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import caimi.web.repository.entity.UserEntity;

@Mapper
public interface UserMapper {

	/**
	 * 只实现用户crud操作，暂且不对权限做区分
	 */
	UserEntity loadUserByUserNmae(@Param("username") String username);
	
	int updateUserEmail(@Param("email") String email, @Param("id") String id);
	
	int updateUserEnabled(@Param("enabled") Boolean enabled, @Param("uid") Long uid);
	
	int deleteUserById(long uid);
	
	UserEntity getUserById(@Param("id") String id);
	
	long reg(UserEntity user);
	
//	List<User> getUserByNickname(@Param("nickname") String nickname);
	
//	int deleteUserRolesByUid(Long id);
	
//	int setUserRoles(@Param("rids") Long[] rids, @Param("id") Long id);
	
}
