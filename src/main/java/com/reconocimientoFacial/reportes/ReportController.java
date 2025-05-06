package com.reconocimientoFacial.reportes;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db_biometria")
public class ReportController {
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    //http://localhost:8080/db_biometria/report
    @GetMapping("/report")
    public ResponseEntity<byte[]> generarReporte() {
        try{
            byte[] report = reportService.generarReport("Registro_Accesos");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=Report.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/ReporteVisitantes")
    public ResponseEntity<byte[]> generarReporteVisitantes() {
        try{
            byte[] report = reportService.generarReporteVisitantes("Reporte_general_visitantes"); // Llamar al nuevo método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteVisitantes.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reporteIndividualV")
    public ResponseEntity<byte[]> generarReporteIndividualVisitante(@RequestParam("id") int id) {
        try {
            byte[] report = reportService.generarReporteIndividualVisitante(id, "Registro_individual_visitantes"); // Nuevo método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteIndividualVisitante.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /*----------------------Reportes Usuario------------*/
    @GetMapping("/reportU")
    public ResponseEntity<byte[]> generarReporteUsuariosGeneral() {
        try{
            byte[] report = reportService.generarReporteUsuariosGeneral("Registro_general_usuarios"); // Llamar al método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteUsuariosGeneral.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reporteIndividualUsuario")
    public ResponseEntity<byte[]> generarReporteIndividualUsuario(@RequestParam("id") int id) {
        try {
            byte[] report = reportService.generarReporteIndividualUsuario(id, "Registro_individual_usuario"); // Llamar al método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteIndividualUsuario.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
