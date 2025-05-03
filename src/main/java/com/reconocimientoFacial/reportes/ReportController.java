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

    @GetMapping("/reporteV")
    public ResponseEntity<byte[]> generarReporteVisitantes() {
        try{
            byte[] report = reportService.generarReporteVisitantes("Reporte_general_registros_usuarios"); // Llamar al nuevo método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteVisitantes.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reporteIndividual")
    public ResponseEntity<byte[]> generarReporteIndividual(@RequestParam("id") int id) {
        try {
            byte[] report = reportService.generarReporteIndividual(id, "reporte_individual"); // Llamar al nuevo método en ReportService
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=ReporteInd.pdf");
            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
