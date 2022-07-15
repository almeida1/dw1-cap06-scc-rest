package com.fatec.sigx.controller;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.sigx.model.Cliente;
import com.fatec.sigx.model.ClienteDTO;
import com.fatec.sigx.model.Endereco;
import com.fatec.sigx.services.MantemClienteI;

@RestController
@RequestMapping("/api/v1/clientes")
public class APIClienteController {
	@Autowired
	MantemClienteI mantemCliente;
	Cliente cliente;
	Logger logger = LogManager.getLogger(this.getClass());
	@PostMapping
	public ResponseEntity<Object> saveCliente(@RequestBody @Valid ClienteDTO clienteDTO) {
		cliente = new Cliente();
		if(mantemCliente.consultaPorCpf(clienteDTO.getCpf()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado");
		}
		
        try {
        	cliente.setDataNascimento(clienteDTO.getDataNascimento());
        } catch(Exception e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        Optional<Endereco> endereco = Optional.ofNullable(mantemCliente.obtemEndereco(clienteDTO.getCep()));
        logger.info(">>>>>> controller post " + clienteDTO.getCep());
        if (endereco.isEmpty()) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CEP invalido");
        }
		return ResponseEntity.status(HttpStatus.CREATED).body(mantemCliente.save(clienteDTO.retornaUmCliente()));
	}
}
