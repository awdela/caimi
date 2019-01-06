package com.caimi.service.repository.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.caimi.util.StringUtil;

@Entity
@Table(name = "user")
public class UserEntity extends AbstractBusinessEntity implements User, UserDetails {

    /**
     * 用户表实体
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "user_password")
    private String passwd;
    
    @Column(name = "user_role")
    private String role;

    @Column(name = "user_title")
    private String title;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_phone")
    private String phone;

//    @ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
//    private List<Role> roles;

    // public List<Role> getRoles() {
    // return roles;
    // }
    //
    // public void setRoles(List<Role> roles) {
    // this.roles = roles;
    // }

    @Override
    public void setPassword(String passwd) {
        this.passwd = passwd;
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
    

	@Override
	public String getRole() {
		return role;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
	}


    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o.getClass() != getClass())) {
            return false;
        }
        UserEntity user = (UserEntity) o;
        return getId().equals(user.getId()) && StringUtil.equals(getName(), user.getName());
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("title", title).put("email", email).put("phone", phone);

        return json;
    }

//    @Override
//    public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
//        super.writeBinary(writer);
//        writer.writeString("title", title);
//        writer.writeString("email", email);
//        writer.writeString("phone", phone);
//    }

//    @Override
//    public void readBinary(BinaryReader reader) throws BinaryObjectException {
//        super.readBinary(reader);
//        title = reader.readString("title");
//        email = reader.readString("email");
//        phone = reader.readString("phone");
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        List<Role> roles = this.getRoles();
//        for (Role role : roles) {
//            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
//        }
        return authorities;
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
