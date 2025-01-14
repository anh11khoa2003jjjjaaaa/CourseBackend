package org.example.sellingcourese.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID", updatable = false, nullable = false)
    private Long userID;

    @Column(name = "DisplayName", nullable = false, length = 255)
    private String displayName;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "Email", length = 255)
    private String email;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "AccountID", length = 255, nullable = false)
    private Long accountID;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID", referencedColumnName = "AccountID", insertable = false, updatable = false)
    private Account account;
   



}
