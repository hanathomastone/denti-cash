package com.kaii.dentix.domain.blockChain.token.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    //ERC20 컨트랙트 주소
    @Column(nullable = false, unique = true, length = 128, name = "contract_address")
    private String contractAddress;
    @Column(nullable = true, length = 100)
    private String name;
    private String tokenName;
    private String tokenSymbol;
    private Long supply;

    @Column(nullable = false)
    private Integer decimals;
    @Column(nullable = false)
    private boolean active;

    /**
     * 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 활성화
     */
    public void activate() {
        this.active = true;
    }
}