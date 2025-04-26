package com.reconocimientoFacial.reportes;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
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

}
