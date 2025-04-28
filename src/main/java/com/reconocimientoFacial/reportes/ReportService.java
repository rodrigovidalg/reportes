package com.reconocimientoFacial.reportes;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private DataSource dataSource;

    public byte[] generarReport(String reportName) throws Exception {
        InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> reportData = getDataFromDatabase(); // Método para obtener los datos

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private List<Map<String, Object>> getDataFromDatabase() throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement();
             java.sql.ResultSet resultSet = statement.executeQuery(
                     "SELECT " +
                             "    ra.id AS id_registro, " +
                             "    u.nombre AS nombre_usuario, " +
                             "    ra.fecha_hora AS fecha_hora, " +
                             "    ra.zona_acceso, " +
                             "    CASE ra.resultado " +
                             "        WHEN 1 THEN 'Exitoso' " +
                             "        WHEN 0 THEN 'Fallido' " +
                             "        ELSE 'Desconocido' " +
                             "    END AS estado_acceso, " +
                             "    ra.metodo AS nombre_metodo " +
                             "FROM " +
                             "    db_biometria.registros_acceso AS ra " +
                             "INNER JOIN " +
                             "    db_biometria.usuarios AS u ON ra.id_usuario = u.id_usuario " +
                             "ORDER BY ra.fecha_hora DESC"
             )) {
            while (resultSet.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("nombre_usuario", resultSet.getString("nombre_usuario"));
                data.put("fecha_hora", resultSet.getString("fecha_hora"));
                data.put("zona_acceso", resultSet.getString("zona_acceso"));
                data.put("estado_acceso", resultSet.getString("estado_acceso"));
                data.put("nombre_metodo", resultSet.getString("nombre_metodo"));
                dataList.add(data);
            }
        }
        return dataList;
    }
}