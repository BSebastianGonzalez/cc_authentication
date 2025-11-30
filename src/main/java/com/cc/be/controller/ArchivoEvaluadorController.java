package com.cc.be.controller;

import com.cc.be.model.ArchivoEvaluador;
import com.cc.be.model.Evaluador;
import com.cc.be.repository.ArchivoEvaluadorRepository;
import com.cc.be.repository.EvaluadorRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluadores/{evaluadorId}/archivos")
public class ArchivoEvaluadorController {

    private final ArchivoEvaluadorRepository archivoRepo;
    private final EvaluadorRepository evaluadorRepo;
    private final Cloudinary cloudinary;

    public ArchivoEvaluadorController(ArchivoEvaluadorRepository archivoRepo,
                                      EvaluadorRepository evaluadorRepo,
                                      Cloudinary cloudinary) {
        this.archivoRepo = archivoRepo;
        this.evaluadorRepo = evaluadorRepo;
        this.cloudinary = cloudinary;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> upload(
            @PathVariable Long evaluadorId,
            @RequestParam("fotocopiaDocumento") MultipartFile fotocopia,
            @RequestParam("certificadosEstudios") MultipartFile certificados,
            @RequestParam("certificadoCuentaBancaria") MultipartFile cuentaBancaria
    ) throws IOException {
        Optional<Evaluador> opt = evaluadorRepo.findById(evaluadorId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Evaluador no encontrado");
        }
        Evaluador evaluador = opt.get();

        ArchivoEvaluador entidades = archivoRepo.findByEvaluadorId(evaluadorId).orElse(new ArchivoEvaluador());
        entidades.setEvaluador(evaluador);

        if (fotocopia != null && !fotocopia.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(fotocopia.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidades.setFotocopiaUrl((String) uploadResult.get("secure_url"));
            entidades.setFotocopiaPublicId((String) uploadResult.get("public_id"));
        }

        if (certificados != null && !certificados.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(certificados.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidades.setCertificadosUrl((String) uploadResult.get("secure_url"));
            entidades.setCertificadosPublicId((String) uploadResult.get("public_id"));
        }

        if (cuentaBancaria != null && !cuentaBancaria.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(cuentaBancaria.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidades.setCuentaBancariaUrl((String) uploadResult.get("secure_url"));
            entidades.setCuentaBancariaPublicId((String) uploadResult.get("public_id"));
        }

        ArchivoEvaluador saved = archivoRepo.save(entidades);
        return ResponseEntity.ok(saved);
    }

    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> update(
            @PathVariable Long evaluadorId,
            @RequestParam(value = "fotocopiaDocumento", required = false) MultipartFile fotocopia,
            @RequestParam(value = "certificadosEstudios", required = false) MultipartFile certificados,
            @RequestParam(value = "certificadoCuentaBancaria", required = false) MultipartFile cuentaBancaria
    ) throws IOException {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("No existen archivos para este evaluador. Use POST para crear.");
        }
        ArchivoEvaluador entidad = opt.get();

        if (fotocopia != null && !fotocopia.isEmpty()) {
            if (entidad.getFotocopiaPublicId() != null) {
                cloudinary.uploader().destroy(entidad.getFotocopiaPublicId(), ObjectUtils.emptyMap());
            }
            Map uploadResult = cloudinary.uploader().upload(fotocopia.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidad.setFotocopiaUrl((String) uploadResult.get("secure_url"));
            entidad.setFotocopiaPublicId((String) uploadResult.get("public_id"));
        }

        if (certificados != null && !certificados.isEmpty()) {
            if (entidad.getCertificadosPublicId() != null) {
                cloudinary.uploader().destroy(entidad.getCertificadosPublicId(), ObjectUtils.emptyMap());
            }
            Map uploadResult = cloudinary.uploader().upload(certificados.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidad.setCertificadosUrl((String) uploadResult.get("secure_url"));
            entidad.setCertificadosPublicId((String) uploadResult.get("public_id"));
        }

        if (cuentaBancaria != null && !cuentaBancaria.isEmpty()) {
            if (entidad.getCuentaBancariaPublicId() != null) {
                cloudinary.uploader().destroy(entidad.getCuentaBancariaPublicId(), ObjectUtils.emptyMap());
            }
            Map uploadResult = cloudinary.uploader().upload(cuentaBancaria.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto", "folder", "evaluadores/" + evaluadorId));
            entidad.setCuentaBancariaUrl((String) uploadResult.get("secure_url"));
            entidad.setCuentaBancariaPublicId((String) uploadResult.get("public_id"));
        }

        ArchivoEvaluador saved = archivoRepo.save(entidad);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long evaluadorId) throws Exception {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ArchivoEvaluador entidad = opt.get();

        if (entidad.getFotocopiaPublicId() != null) {
            cloudinary.uploader().destroy(entidad.getFotocopiaPublicId(), ObjectUtils.emptyMap());
        }
        if (entidad.getCertificadosPublicId() != null) {
            cloudinary.uploader().destroy(entidad.getCertificadosPublicId(), ObjectUtils.emptyMap());
        }
        if (entidad.getCuentaBancariaPublicId() != null) {
            cloudinary.uploader().destroy(entidad.getCuentaBancariaPublicId(), ObjectUtils.emptyMap());
        }

        archivoRepo.delete(entidad);
        return ResponseEntity.noContent().build();
    }

    // --- Nuevos endpoints para obtener cada archivo por evaluadorId ---

    @GetMapping("/fotocopia")
    public ResponseEntity<Void> getFotocopia(@PathVariable Long evaluadorId) {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        if (opt.isEmpty() || opt.get().getFotocopiaUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        URI uri = URI.create(opt.get().getFotocopiaUrl());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    @GetMapping("/certificados")
    public ResponseEntity<Void> getCertificados(@PathVariable Long evaluadorId) {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        if (opt.isEmpty() || opt.get().getCertificadosUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        URI uri = URI.create(opt.get().getCertificadosUrl());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    @GetMapping("/cuenta-bancaria")
    public ResponseEntity<Void> getCuentaBancaria(@PathVariable Long evaluadorId) {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        if (opt.isEmpty() || opt.get().getCuentaBancariaUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        URI uri = URI.create(opt.get().getCuentaBancariaUrl());
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    // Opcional: endpoint para devolver los metadatos (URLs) en JSON
    @GetMapping
    public ResponseEntity<?> getArchivos(@PathVariable Long evaluadorId) {
        Optional<ArchivoEvaluador> opt = archivoRepo.findByEvaluadorId(evaluadorId);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
