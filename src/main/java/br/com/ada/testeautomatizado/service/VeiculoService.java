package br.com.ada.testeautomatizado.service;

import br.com.ada.testeautomatizado.dto.VeiculoDTO;
import br.com.ada.testeautomatizado.exception.PlacaInvalidaException;
import br.com.ada.testeautomatizado.exception.VeiculoNaoEncontradoException;
import br.com.ada.testeautomatizado.model.Veiculo;
import br.com.ada.testeautomatizado.repository.VeiculoRepository;
import br.com.ada.testeautomatizado.util.Response;
import br.com.ada.testeautomatizado.util.ValidacaoPlaca;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ValidacaoPlaca validacaoPlaca;

    public ResponseEntity<Response<VeiculoDTO>> cadastrar(VeiculoDTO veiculoDTO) {
        try {
            validacaoPlaca.isPlacaValida(veiculoDTO.getPlaca());
            Veiculo veiculo = new Veiculo();
            veiculo.setDataFabricacao(veiculoDTO.getDataFabricacao());
            veiculo.setModelo(veiculoDTO.getModelo());
            veiculo.setDisponivel(veiculoDTO.getDisponivel());
            veiculo.setPlaca(veiculoDTO.getPlaca());
            veiculo.setModelo(veiculoDTO.getModelo());
            return ResponseEntity.ok(new Response<VeiculoDTO>("Sucesso", veiculoDTO));
        } catch (PlacaInvalidaException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Response<>(e.getMessage(), null));
        }
    }

    public ResponseEntity<Response<Boolean>> deletarVeiculoPelaPlaca(String placa) {

        try {
            buscarVeiculoPelaPlaca(placa).ifPresent(this.veiculoRepository::delete);
            return ResponseEntity.ok(new Response<>("Sucesso", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Response<VeiculoDTO>> atualizar(VeiculoDTO veiculoAtualizadoDTO) {

        try {
            veiculoAtualizadoDTO.setDisponivel(Boolean.FALSE);
            validacaoPlaca.isPlacaValida(veiculoAtualizadoDTO.getPlaca());
            Veiculo veiculo = buscarVeiculoPelaPlaca(veiculoAtualizadoDTO.getPlaca()).get();
            veiculo.setDataFabricacao(veiculoAtualizadoDTO.getDataFabricacao());
            veiculo.setModelo(veiculoAtualizadoDTO.getModelo());
            veiculo.setDisponivel(veiculoAtualizadoDTO.getDisponivel());
            veiculo.setPlaca(veiculoAtualizadoDTO.getPlaca());
            veiculo.setModelo(veiculoAtualizadoDTO.getModelo());
            veiculoRepository.save(veiculo);
            return ResponseEntity.ok(new Response<>("Sucesso", veiculoAtualizadoDTO));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Response<>(e.getMessage(), null));
        }

    }

    public ResponseEntity<Response<List<VeiculoDTO>>> listarTodos() {
        List<VeiculoDTO> veiculosDTO = veiculoRepository.findAll().stream()
                .map(veiculo -> {
                    VeiculoDTO veiculoDTO = new VeiculoDTO();
                    veiculoDTO.setPlaca(veiculo.getPlaca());
                    veiculoDTO.setModelo(veiculo.getModelo());
                    veiculoDTO.setMarca(veiculo.getMarca());
                    veiculoDTO.setDataFabricacao(veiculo.getDataFabricacao());
                    veiculoDTO.setDisponivel(veiculo.getDisponivel());
                    return veiculoDTO;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(new Response<>("Sucesso", veiculosDTO));
    }

    private Optional<Veiculo> buscarVeiculoPelaPlaca(String placa) {
        return this.veiculoRepository.findByPlaca(placa);
    }
}

