package com.example.project.supplier;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.project.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class SupplierIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSuppliersWithFiltersAndPagination() throws Exception {
        mockMvc.perform(get("/api/suppliers")
                        .header("Authorization", bearer(login("admin@example.com", "password123")))
                        .param("keyword", "台灣")
                        .param("status", "ACTIVE")
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].code").value("SUP-001"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnSupplierDetailForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/suppliers/1")
                        .header("Authorization", bearer(login("user1@example.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUP-001"))
                .andExpect(jsonPath("$.name").value("台灣材料有限公司"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldCreateSupplierForAdmin() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .header("Authorization", bearer(login("admin@example.com", "password123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUP-010",
                                  "name": "新供應商股份有限公司",
                                  "contactEmail": "hello@supplier.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.code").value("SUP-010"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldUpdateSupplierForAdmin() throws Exception {
        mockMvc.perform(patch("/api/suppliers/1")
                        .header("Authorization", bearer(login("admin@example.com", "password123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUP-001",
                                  "name": "台灣材料測試有限公司",
                                  "contactEmail": "updated@twmaterial.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("台灣材料測試有限公司"))
                .andExpect(jsonPath("$.contactEmail").value("updated@twmaterial.com"));
    }

    @Test
    void shouldDeleteSupplierForAdmin() throws Exception {
        String adminToken = login("admin@example.com", "password123");

        mockMvc.perform(delete("/api/suppliers/2")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/suppliers/2")
                        .header("Authorization", bearer(adminToken))
                        .header("X-Request-Id", "req-supplier-delete"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Request-Id", "req-supplier-delete"))
                .andExpect(jsonPath("$.errorCode").value("SUPPLIER_NOT_FOUND"))
                .andExpect(jsonPath("$.requestId").value("req-supplier-delete"));
    }

    @Test
    void shouldRejectSupplierMutationForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .header("Authorization", bearer(login("user1@example.com", "password123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUP-010",
                                  "name": "新供應商股份有限公司",
                                  "contactEmail": "hello@supplier.com"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("SUPPLIER_MANAGE_FORBIDDEN"));
    }

    @Test
    void shouldRejectDuplicatedSupplierCode() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .header("Authorization", bearer(login("admin@example.com", "password123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "SUP-001",
                                  "name": "Duplicate Supplier",
                                  "contactEmail": "duplicate@supplier.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("SUPPLIER_CODE_DUPLICATED"));
    }

    private String login(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        return json.get("accessToken").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
