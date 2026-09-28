package vn.huynhtoantravel.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=190) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Column(name="full_name", nullable=false, length=150) private String fullName;
    @Column(length=20) private String phone;
    @Column(nullable=false) private boolean enabled=true;
    @Column(name="created_at", insertable=false, updatable=false) private LocalDateTime createdAt;
    
    // Bắt các cột cũ trong Database để không bị lỗi NOT NULL
    @Column(name="password") private String legacyPassword = "";
    @Column(name="role") private String legacyRole = "USER";
    @Column(name="username") private String legacyUsername = java.util.UUID.randomUUID().toString();

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="user_roles", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roles=new HashSet<>();
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
    public String getFullName(){return fullName;} public void setFullName(String fullName){this.fullName=fullName;}
    public String getPhone(){return phone;} public void setPhone(String phone){this.phone=phone;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean enabled){this.enabled=enabled;}
    public Set<Role> getRoles(){return roles;} public void setRoles(Set<Role> roles){this.roles=roles;}
}
