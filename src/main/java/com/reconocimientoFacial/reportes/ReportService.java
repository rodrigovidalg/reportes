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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public byte[] generarReporteIndividual(int idRegistro, String reportName) throws Exception {
        try {
            InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("idRegistro", idRegistro); // Pasar el ID como parámetro al reporte

            List<Map<String, Object>> reportData = getDataForIndividualReport(idRegistro); // Nuevo método para obtener datos filtrados

            JRBeanCollectionDataSource dataSourceJR = new JRBeanCollectionDataSource(reportData);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceJR);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza de la pila en la consola
            throw e;
        }
    }

    private List<Map<String, Object>> getDataForIndividualReport(int idRegistro) throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(
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
                             "    ra.metodo AS nombre_metodo " + // Omitting imagen for now
                             "FROM " +
                             "    db_biometria.registros_acceso AS ra " +
                             "INNER JOIN " +
                             "    db_biometria.usuarios AS u ON ra.id_usuario = u.id_usuario " +
                             "WHERE ra.id = ?"
             )) {
            preparedStatement.setInt(1, idRegistro);
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id_registro", resultSet.getInt("id_registro"));
                    data.put("nombre_usuario", resultSet.getString("nombre_usuario"));
                    data.put("fecha_hora", resultSet.getString("fecha_hora"));
                    data.put("zona_acceso", resultSet.getString("zona_acceso"));
                    data.put("estado_acceso", resultSet.getString("estado_acceso"));
                    data.put("nombre_metodo", resultSet.getString("nombre_metodo"));
                    dataList.add(data);
                }
            }
        }
        return dataList;
    }

    //---------------------------Reporte Visitantes----------------
    public byte[] generarReporteVisitantes(String reportName) throws Exception {
        InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> reportData = getDataFromDatabaseVisitantes(); // Nuevo método para obtener datos de visitantes

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    private List<Map<String, Object>> getDataFromDatabaseVisitantes() throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement();
             java.sql.ResultSet resultSet = statement.executeQuery(
                     "SELECT " +
                             "    rav.id AS id_registro, " +
                             "    CONCAT(v.nombres, ' ', v.apellidos) AS nombre_completo, " +
                             "    rav.fecha_hora AS fecha_hora, " +
                             "    d.nombre AS zona_acceso, " +
                             "    CASE rav.resultado " +
                             "        WHEN 0 THEN 'Fallido' " +
                             "        WHEN 1 THEN 'Exitoso' " +
                             "        ELSE 'Desconocido' " +
                             "    END AS estado_acceso, " +
                             "    m.metodo AS metodo " +
                             "FROM registros_acceso_visitante AS rav " +
                             "INNER JOIN visitantes AS v ON rav.id_visitante = v.id_visitante " +
                             "INNER JOIN departamentos AS d ON rav.zona_acceso = d.id_departamento " +
                             "INNER JOIN metodos AS m ON rav.id_metodo = m.id_metodo " +
                             "ORDER BY rav.fecha_hora DESC"
             )) {
            while (resultSet.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("id_registro", resultSet.getInt("id_registro"));
                data.put("nombre_completo", resultSet.getString("nombre_completo"));
                data.put("fecha_hora", resultSet.getString("fecha_hora"));
                data.put("zona_acceso", resultSet.getString("zona_acceso"));
                data.put("estado_acceso", resultSet.getString("estado_acceso"));
                data.put("metodo", resultSet.getString("metodo"));
                dataList.add(data);
            }
        }
        return dataList;
    }
//new java.io.ByteArrayInputStream($F{imagen})
    /*------------------------Reporte Individual Visitantes--------------*/
    public byte[] generarReporteIndividualVisitante(int idRegistro, String reportName) throws Exception {
        try {
            InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("IdRegistro", idRegistro); // Pasar el ID como parámetro

            List<Map<String, Object>> reportData = getDataForIndividualReportVisitante(idRegistro);

            JRBeanCollectionDataSource dataSourceJR = new JRBeanCollectionDataSource(reportData);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceJR);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<Map<String, Object>> getDataForIndividualReportVisitante(int idRegistro) throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT  " +
                             "    rav.id AS id_registro, " +
                             "    CONCAT(v.nombres, ' ', v.apellidos) AS nombre_completo,  " +
                             "    rav.fecha_hora AS fecha_hora,  " +
                             "    d.nombre AS zona_acceso,  " +
                             "    CASE rav.resultado  " +
                             "        WHEN 0 THEN 'Fallido'  " +
                             "        WHEN 1 THEN 'Exitoso'  " +
                             "        ELSE 'Desconocido'  " +
                             "    END AS estado_acceso,  " +
                             "    m.metodo AS metodo,  " +
                             "    rav.encoding_facial AS imagen " + // Incluimos la imagen
                             "FROM registros_acceso_visitante AS rav  " +
                             "INNER JOIN visitantes AS v ON rav.id_visitante = v.id_visitante  " +
                             "INNER JOIN departamentos AS d ON rav.zona_acceso = d.id_departamento  " +
                             "INNER JOIN metodos AS m ON rav.id_metodo = m.id_metodo " +
                             "WHERE rav.id = ?"
             )) {
            preparedStatement.setInt(1, idRegistro);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id_registro", resultSet.getInt("id_registro"));
                    data.put("nombre_completo", resultSet.getString("nombre_completo"));
                    data.put("fecha_hora", resultSet.getString("fecha_hora"));
                    data.put("zona_acceso", resultSet.getString("zona_acceso"));
                    data.put("estado_acceso", resultSet.getString("estado_acceso"));
                    data.put("metodo", resultSet.getString("metodo"));
                    data.put("imagen", resultSet.getBytes("imagen")); // Obtenemos la imagen como byte[]
                    dataList.add(data);
                }
            }
        }
        return dataList;
    }


    /*--------------------------------------*/
    public byte[] generarReporteUsuariosGeneral(String reportName) throws Exception {
        InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> reportData = getDataFromDatabaseUsuariosGeneral(); // Nuevo método para obtener datos de usuarios

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportData);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private List<Map<String, Object>> getDataFromDatabaseUsuariosGeneral() throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement();
             java.sql.ResultSet resultSet = statement.executeQuery(
                     "SELECT  " +
                             "    rau.id AS id_registro, " +
                             "    CONCAT(u.nombres, ' ', u.apellidos) AS nombre_completo, " +
                             "    rau.fecha_hora AS fecha_hora, " +
                             "    d.nombre AS  zona_acceso, " +
                             "    CASE rau.resultado " +
                             "        WHEN 0 THEN 'Fallido' " +
                             "        WHEN 1 THEN 'Existoso' " +
                             "        ELSE 'Desconocido' " +
                             "    END AS estado_acceso, " +
                             "    m.metodo, " +
                             "    rau.encoding_facial as imagen " +
                             "FROM registros_acceso_usuario AS rau " +
                             "INNER JOIN usuarios AS u ON rau.id_usuario = u.id_usuario " +
                             "INNER JOIN departamentos AS d ON rau.zona_acceso = d.id_departamento " +
                             "INNER JOIN metodos AS m ON rau.id_metodo = m.id_metodo " +
                             "ORDER BY rau.fecha_hora DESC"
             )) {
            while (resultSet.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("id_registro", resultSet.getInt("id_registro"));
                data.put("nombre_completo", resultSet.getString("nombre_completo"));
                data.put("fecha_hora", resultSet.getString("fecha_hora"));
                data.put("zona_acceso", resultSet.getString("zona_acceso"));
                data.put("estado_acceso", resultSet.getString("estado_acceso"));
                data.put("metodo", resultSet.getString("metodo"));
                data.put("imagen", resultSet.getBytes("imagen"));
                dataList.add(data);
            }
        }
        return dataList;
    }

    /*------------------Reportes individuales usuarios-----------------*/
    public byte[] generarReporteIndividualUsuario(int idRegistro, String reportName) throws Exception {
        try {
            InputStream file = resourceLoader.getResource("classpath:reports/" + reportName + ".jasper").getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("IdRegistro", idRegistro); // Pasar el ID como parámetro

            List<Map<String, Object>> reportData = getDataForIndividualReportUsuario(idRegistro);

            JRBeanCollectionDataSource dataSourceJR = new JRBeanCollectionDataSource(reportData);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSourceJR);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<Map<String, Object>> getDataForIndividualReportUsuario(int idRegistro) throws SQLException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT  " +
                             "    rau.id AS id_registro, " +
                             "    CONCAT(u.nombres, ' ', u.apellidos) AS nombre_completo, " +
                             "    rau.fecha_hora AS fecha_hora, " +
                             "    d.nombre AS  zona_acceso, " +
                             "    CASE rau.resultado " +
                             "        WHEN 0 THEN 'Fallido' " +
                             "        WHEN 1 THEN 'Existoso' " +
                             "        ELSE 'Desconocido' " +
                             "    END AS estado_acceso, " +
                             "    m.metodo, " +
                             "    rau.encoding_facial as imagen " +
                             "FROM registros_acceso_usuario AS rau " +
                             "INNER JOIN usuarios AS u ON rau.id_usuario = u.id_usuario " +
                             "INNER JOIN departamentos AS d ON rau.zona_acceso = d.id_departamento " +
                             "INNER JOIN metodos AS m ON rau.id_metodo = m.id_metodo " +
                             "WHERE rau.id = ?"
             )) {
            preparedStatement.setInt(1, idRegistro);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id_registro", resultSet.getInt("id_registro"));
                    data.put("nombre_completo", resultSet.getString("nombre_completo"));
                    data.put("fecha_hora", resultSet.getString("fecha_hora"));
                    data.put("zona_acceso", resultSet.getString("zona_acceso"));
                    data.put("estado_acceso", resultSet.getString("estado_acceso"));
                    data.put("metodo", resultSet.getString("metodo"));
                    data.put("imagen", resultSet.getBytes("imagen"));
                    dataList.add(data);
                }
            }
        }
        return dataList;
    }
}