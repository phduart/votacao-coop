package com.br.duarte.votacao.domain.service;

import com.br.duarte.votacao.api.dto.request.PautaRequest;
import com.br.duarte.votacao.api.dto.response.PautaResponse;
import com.br.duarte.votacao.api.exception.BusinessException;
import com.br.duarte.votacao.api.mapper.PautaMapper;
import com.br.duarte.votacao.domain.entity.Pauta;
import com.br.duarte.votacao.domain.repository.PautaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;
    private final PautaMapper pautaMapper;

    @Transactional
    public PautaResponse salvar(PautaRequest request) {
        Pauta pauta = pautaMapper.toEntity(request);
        pauta = pautaRepository.save(pauta);
        return pautaMapper.toResponse(pauta);
    }

    @Transactional(readOnly = true)
    public PautaResponse buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .map(pautaMapper::toResponse)
                .orElseThrow(() -> new BusinessException("Pauta não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<PautaResponse> listarTodas() {
        return pautaRepository.findAll().stream()
                .map(pautaMapper::toResponse)
                .collect(Collectors.toList());
    }
}
