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
    @Column(name = "contract_id") // ✅ FK가 여기를 참조해야 함
    private Long id;

    // ✅ ERC20 컨트랙트 주소
    @Column(nullable = false, unique = true, length = 128, name="contract_address")
    private String contractAddress;

    @Column(nullable = false)
    private String name; // 예: DENTI Token


    @Column(nullable = false)
    private int decimals = 18;


    @Column(nullable = false)
    private boolean active;
}