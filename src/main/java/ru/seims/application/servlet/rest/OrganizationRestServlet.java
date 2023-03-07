package ru.seims.application.servlet.rest;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.jni.OS;
import org.springframework.web.bind.annotation.*;
import ru.seims.application.configuration.WebSecurityConfiguration;
import ru.seims.application.servlet.jsp.OrganizationServlet;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.entitiy.Organization;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.json.JSONBuilder;
import ru.seims.utils.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.util.ArrayList;

@RestController
@RequestMapping()
public class OrganizationRestServlet {
    public static final String getOrg = "/api/org/get/{id}";
    public static final String getRegion = "/api/region/{region}";
    public static String organizationTableName = "organizations";
    public static final String transportImage = WebSecurityConfiguration.orgEditorPattern+"org/{id}/prepare/image/{img}";
    @GetMapping(getOrg)
    public Organization getOrgById(@PathVariable String id) {
        return getOrganizationById(id);
    }

    @GetMapping(getRegion)
    @ResponseBody
    public ArrayList<DataTable> getRegionData(@PathVariable String region) {
        if (region == null || region.isEmpty())
            region = "Аннинский";
        try {
            ArrayList<DataTable> tablesData = new ArrayList<>();
            SQLExecutor executor = SQLExecutor.getInstance();
            File queryDir = new File(FileResourcesUtils.RESOURCE_PATH + executor.SQL_RESOURCE_PATH + "/doo_VR_region");
            for (final File query : queryDir.listFiles()) {
                if (query != null && query.isFile()) {
                    ResultSet tableData = executor.executeSelect(
                            executor.loadSqlResource("doo_VR_region/" + query.getName()), region
                    );
                    //OrganizationServlet.generateTableToFromResultSet(tablesData, executor, tableData);
                }
            }
            return tablesData;
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 2);
        }
        return null;
    }

    @PostMapping(transportImage)
    @ResponseBody
    public String transportImage(@PathVariable String id, @PathVariable String img) throws IOException {
        if(!OrganizationServlet.validateId(id) || !OrganizationServlet.validateId(img)) {
            return new JSONBuilder().addAVP("status", "error").addAVP("message", "invalid id").getString();
        }
        String fileName = OrganizationServlet.ORG_IMG_FILE_NAME + OrganizationServlet.ORG_IMG_FILE_EXT;
        String filePath = FileResourcesUtils.UPLOAD_PATH + "/" + id + "/" + fileName;
        String outPath = "/img/"+id;
        File outDir = new File(outPath);
        if(!outDir.exists() || !outDir.isDirectory())
            outDir.mkdir();
        outPath += "/" + fileName;
        File src = new File(filePath);
        File dest = new File(outPath);
        if(!dest.exists())
            FileUtils.copyFile(src, dest);
        return new JSONBuilder().addAVP("status", "success").getString();
    }

    public static Organization getOrganizationById(String id) {
        if(id.isEmpty())
            return null;
        try {
            SQLExecutor executor = SQLExecutor.getInstance();
            ResultSet rs = executor.executeSelectSimple(organizationTableName, "*", "id like '"+ id +"'");
            if(rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String district = rs.getString("district");
                String pageId = "";
                return new Organization(id, type, name, district, pageId);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
