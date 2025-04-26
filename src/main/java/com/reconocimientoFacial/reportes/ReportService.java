package com.reconocimientoFacial.reportes;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static net.sf.jasperreports.engine.JasperFillManager.fillReport;

@Service
public class ReportService {
    private final DataSource dataSource;

    public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte [] generarReport(String reportName)  throws Exception {
        //cargar el reporte
        InputStream reportStream = this.getClass().getResourceAsStream("/reports/"+reportName+".jasper");

        Map<String, Object> parms = new HashMap<>();
        //Llenado
        JasperPrint jasperPrint = fillReport(reportStream,parms, dataSource.getConnection());
        //Exportación a un reporte de tipo PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
