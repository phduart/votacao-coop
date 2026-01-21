package com.br.duarte.votacao.api.v1.mapper;

import com.br.duarte.votacao.api.v1.dto.request.PautaRequest;
import com.br.duarte.votacao.api.v1.dto.response.PautaResponse;
import com.br.duarte.votacao.domain.entity.Pauta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PautaMapper {
    Pauta toEntity(PautaRequest request);
    PautaResponse toResponse(Pauta pauta);
}
