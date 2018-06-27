package com.caimi.service.repository.entity.cache;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.caimi.service.repository.entity.AbstractBusinessEntity;
import com.caimi.service.repository.entity.User;
import com.caimi.util.StringUtil;

@Entity
@Table(name = "user")
public class UserEntity extends AbstractBusinessEntity implements User, UserDetails, Binarylizable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "user_passwd")
	private String passwd;
	
	@Column(name = "ug_id")
    private String departmentId;

    @Column(name = "user_title")
    private String title;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_phone")
    private String phone;
    
    private List<? extends GrantedAuthority> authorities;
    
    public void setPassWord(String passwd) {
    	this.passwd = passwd;
    }
    
    public void setDepartmentId(String departmentId) {
    	this.departmentId = departmentId;
    }
    
	@Override
	public String getDepartmentId() {
		return departmentId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String getEmail() {
		return email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Override
	public String getPhone() {
		return phone;
	}

	 @Override
	 public boolean equals(Object o){
		 if ( o==null || !(o.getClass()!=getClass())){
			 return false;
		 }	
		 UserEntity user = (UserEntity)o;
		 return getId().equals(user.getId()) && StringUtil.equals(getName(), user.getName());
	 }

	 @Override
	 public JSONObject toJSON() {
		 JSONObject json = super.toJSON();
		 
		 json
		 .put("departmentId", getDepartmentId())
		 .put("title", title)
		 .put("email", email)
		 .put("phone", phone);
		 
		 return json;
	 }

	 @Override
	 public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
		 super.writeBinary(writer);
		 writer.writeString("departmentId", departmentId);
		 writer.writeString("title", title);
		 writer.writeString("email", email);
		 writer.writeString("phone", phone);
	 }

	 @Override
	 public void readBinary(BinaryReader reader) throws BinaryObjectException {
		 super.readBinary(reader);
		 departmentId = reader.readString("departmentId");
		 title = reader.readString("title");
		 email = reader.readString("email");
		 phone = reader.readString("phone");
	 }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public void getAuthorities(List<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String getPassword() {
		return passwd;
	}

	@Override
	public String getUsername() {
		return name;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
