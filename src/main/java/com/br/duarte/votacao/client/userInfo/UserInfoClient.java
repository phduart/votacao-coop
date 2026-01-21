package com.br.duarte.votacao.client.userInfo;

import com.br.duarte.votacao.client.userInfo.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userInfoClient", url = "https://user-info.herokuapp.com")
public interface UserInfoClient {

    @GetMapping("/users/{cpf}")
    UserInfoResponse checkCpfStatus(@PathVariable("cpf") String cpf);
}
