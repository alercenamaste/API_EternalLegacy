/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;

/**
 *
 * @author mauricio.olguin
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class AdminCatalogController {

    @POST
    @Path("/catalogProducts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducts(CatalogProductRequest request) {

        String language = request.getLanguage();

        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }

        ArrayList<CatalogProductDTO> productos = new ArrayList<>();

        String sql
                = "SELECT "
                + "p.id,"
                + "p.code,"
                + "p.price,"
                + "p.currency,"
                + "p.stock,"
                + "p.quality,"
                + "p.featured,"
                + "t.product_name,"
                + "t.short_description,"
                + "i.image_url "
                + "FROM catalog_product p "
                + "INNER JOIN catalog_translation t ON p.id=t.product_id "
                + "INNER JOIN catalog_image i ON p.id=i.product_id "
                + "WHERE "
                + "p.active=true "
                + "AND i.cover=true "
                + "AND i.active=true "
                + "AND t.language_code=? "
                + "ORDER BY "
                + "p.featured DESC,"
                + "p.quality DESC,"
                + "p.price ASC";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setString(1, language);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                CatalogProductDTO dto = new CatalogProductDTO();

                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setName(rs.getString("product_name"));
                dto.setShortDescription(rs.getString("short_description"));
                dto.setPrice(rs.getDouble("price"));
                dto.setCurrency(rs.getString("currency"));
                dto.setStock(rs.getInt("stock"));
                dto.setQuality(rs.getInt("quality"));
                dto.setImage(rs.getString("image_url"));
                dto.setFeatured(rs.getBoolean("featured"));

                productos.add(dto);

            }

            return Response.ok(productos)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        } catch (SQLException ex) {

            ex.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();

        }

    }

   /* @POST
    @Path("/catalogFeatured")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeaturedProducts(CatalogFeaturedRequest request) {

        String language = request.getLanguage();

        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }

        ArrayList<CatalogFeaturedDTO> productos = new ArrayList<>();

        String sql
                = "SELECT "
                + "p.id,"
                + "p.code,"
                + "p.price,"
                + "p.currency,"
                + "t.product_name,"
                + "t.short_description,"
                + "i.image_url "
                + "FROM catalog_product p "
                + "INNER JOIN catalog_translation t "
                + "ON p.id=t.product_id "
                + "INNER JOIN catalog_image i "
                + "ON p.id=i.product_id "
                + "WHERE "
                + "p.featured=true "
                + "AND p.active=true "
                + "AND i.cover=true "
                + "AND i.active=true "
                + "AND t.language_code=? "
                + "ORDER BY "
                + "p.quality DESC,"
                + "p.price ASC "
                + "LIMIT 6";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);) {

            stmt.setString(1, language);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                CatalogFeaturedDTO dto = new CatalogFeaturedDTO();

                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setProductName(rs.getString("product_name"));
                dto.setShortDescription(rs.getString("short_description"));
                dto.setImage(rs.getString("image_url"));
                dto.setPrice(rs.getDouble("price"));
                dto.setCurrency(rs.getString("currency"));

                productos.add(dto);

            }

            CatalogHomeResponse response = new CatalogHomeResponse();

            response.setFeatured(productos);

            return Response.ok(response)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();

        }

    }*/

    @POST
    @Path("/catalogProductDetail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductDetail(CatalogProductDetailRequest request) {

        if (request.getProductId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"productId is required\"}")
                    .build();
        }

        String language = request.getLanguage();

        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }

        CatalogProductDetailDTO product = new CatalogProductDetailDTO();

        try (Connection connection = DatabaseConnection.getConnection()) {

            //----------------------------------------------------
            // DATOS DEL PRODUCTO
            //----------------------------------------------------
            String sqlProduct
                    = "SELECT "
                    + "p.id,"
                    + "p.code,"
                    + "p.price,"
                    + "p.currency,"
                    + "p.stock,"
                    + "p.quality,"
                    + "p.featured,"
                    + "t.product_name,"
                    + "t.short_description,"
                    + "t.long_description "
                    + "FROM catalog_product p "
                    + "INNER JOIN catalog_translation t ON p.id=t.product_id "
                    + "WHERE p.id=? "
                    + "AND t.language_code=?";

            try (PreparedStatement stmt = connection.prepareStatement(sqlProduct)) {

                stmt.setInt(1, request.getProductId());
                stmt.setString(2, language);

                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {

                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\":\"Product not found\"}")
                            .build();

                }

                product.setId(rs.getInt("id"));
                product.setCode(rs.getString("code"));
                product.setProductName(rs.getString("product_name"));
                product.setShortDescription(rs.getString("short_description"));
                product.setLongDescription(rs.getString("long_description"));
                product.setPrice(rs.getDouble("price"));
                product.setCurrency(rs.getString("currency"));
                product.setStock(rs.getInt("stock"));
                product.setQuality(rs.getInt("quality"));
                product.setFeatured(rs.getBoolean("featured"));

            }

            //----------------------------------------------------
            // IMÁGENES
            //----------------------------------------------------
            String sqlImages
                    = "SELECT "
                    + "id,"
                    + "image_url,"
                    + "image_order,"
                    + "cover "
                    + "FROM catalog_image "
                    + "WHERE product_id=? "
                    + "AND active=true "
                    + "ORDER BY image_order";

            try (PreparedStatement stmt = connection.prepareStatement(sqlImages)) {

                stmt.setInt(1, request.getProductId());

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {

                    CatalogImageDTO image = new CatalogImageDTO();

                    image.setId(rs.getInt("id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setOrder(rs.getInt("image_order"));
                    image.setCover(rs.getBoolean("cover"));

                    product.getImages().add(image);

                }

            }

            return Response.ok(product)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();

        }

    }

    @POST
    @Path("/catalogFeatured")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFeaturedProducts(CatalogFeaturedRequest request) {

        String language = request.getLanguage();

        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }

        ArrayList<CatalogFeaturedDTO> list = new ArrayList<>();

        String sql
                = "SELECT "
                + "p.id,"
                + "p.code,"
                + "p.price,"
                + "p.currency,"
                + "p.quality,"
                + "t.product_name,"
                + "t.short_description,"
                + "i.image_url "
                + "FROM catalog_product p "
                + "INNER JOIN catalog_translation t ON p.id=t.product_id "
                + "LEFT JOIN catalog_image i ON p.id=i.product_id "
                + "AND i.cover=true "
                + "AND i.active=true "
                + "WHERE p.active=true "
                + "AND p.featured=true "
                + "AND t.language_code=? "
                + "ORDER BY p.quality DESC,p.price ASC "
                + "LIMIT 10";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ps.setString(1, language);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CatalogFeaturedDTO dto = new CatalogFeaturedDTO();

                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setProductName(rs.getString("product_name"));
                dto.setShortDescription(rs.getString("short_description"));
                dto.setPrice(rs.getDouble("price"));
                dto.setCurrency(rs.getString("currency"));
                dto.setQuality(rs.getInt("quality"));
                dto.setImageUrl(rs.getString("image_url"));

                list.add(dto);

            }

            return Response.ok(list)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            return Response.serverError()
                    .entity(ex.getMessage())
                    .build();

        }

    }
/*
    @POST
    @Path("/catalogCategories") 
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {

        ArrayList<CatalogCategoryDTO> categorias = new ArrayList<>();

        String sql
                = "SELECT id,code,icon "
                + "FROM catalog_category "
                + "WHERE active=true "
                + "ORDER BY code";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CatalogCategoryDTO dto = new CatalogCategoryDTO();

                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setIcon(rs.getString("icon"));

                categorias.add(dto);

            }

            return Response.ok(categorias)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            return Response.serverError()
                    .entity(ex.getMessage())
                    .build();

        }

    }*/

    @POST
    @Path("/catalogCategories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(CatalogCategoryRequest request) {

        String language = request.getLanguage();

        if (language == null || language.trim().isEmpty()) {
            language = "en";
        }

        ArrayList<CatalogCategoryDTO> categories = new ArrayList<>();

        String sql
                = "SELECT "
                + "c.id,"
                + "c.code,"
                + "c.icon,"
                + "t.product_name "
                + "FROM catalog_category c "
                + "INNER JOIN catalog_product p ON p.category_id=c.id "
                + "INNER JOIN catalog_translation t ON t.product_id=p.id "
                + "WHERE "
                + "c.active=true "
                + "AND t.language_code=? "
                + "GROUP BY c.id,c.code,c.icon,t.product_name "
                + "ORDER BY c.code";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);) {

            statement.setString(1, language);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                CatalogCategoryDTO dto = new CatalogCategoryDTO();

                dto.setId(rs.getInt("id"));
                dto.setCode(rs.getString("code"));
                dto.setIcon(rs.getString("icon"));
                dto.setName(rs.getString("product_name"));

                categories.add(dto);

            }

            return Response.ok(categories)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .build();

        } catch (Exception ex) {

            ex.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();

        }

    }

}//principañ 
