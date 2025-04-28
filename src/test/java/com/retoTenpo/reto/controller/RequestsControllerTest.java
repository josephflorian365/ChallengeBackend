package com.retoTenpo.reto.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.retoTenpo.reto.controller.request.ValuesRequest;
import com.retoTenpo.reto.controller.response.HistoryResponse;
import com.retoTenpo.reto.controller.response.PagedResponse;
import com.retoTenpo.reto.controller.response.ValuesResponse;
import com.retoTenpo.reto.repository.HistoryRepository;
import com.retoTenpo.reto.service.ConsultService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient(timeout = "50000")
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = RequestsController.class)
@ActiveProfiles(value = "test")
class RequestsControllerTest {

  public static final String PATH_API = "/api";

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ConsultService consultService;

  @MockitoBean
  private HistoryRepository historyRepository;

  @Test
  void givenInputValues_whenCalculateSum_thenReturnOk() {
    // Arrange: preparar el request y la respuesta esperada
    ValuesRequest request = new ValuesRequest(new BigDecimal("5.5"), new BigDecimal("4.5"));
    ValuesResponse response = new ValuesResponse(new BigDecimal("10.0"));

    // Mock del service
    when(consultService.calculateSum(any(), any(Boolean.class), any()))
        .thenReturn(Mono.just(response));

    // Act + Assert: llamada al endpoint
    webTestClient.post()
        .uri("/api/sum")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.answer").isEqualTo(10.0);
  }

  @Test
  void givenInputValues_whenGetHistory_thenReturnOk() {
    // Simulación de datos de respuesta
    HistoryResponse history1 = HistoryResponse.builder()
        .date(LocalDateTime.now())
        .endPoint("/api/sum")
        .parameters("{\"num1\":1,\"num2\":2}")
        .answer(new BigDecimal("3.00"))
        .build();

    HistoryResponse history2 = HistoryResponse.builder()
        .date(LocalDateTime.now())
        .endPoint("/api/sum")
        .parameters("{\"num1\":5,\"num2\":5}")
        .answer(new BigDecimal("10.00"))
        .error(null)
        .build();

    List<HistoryResponse> historyList = List.of(history1, history2);
    PagedResponse<HistoryResponse> pagedResponse = new PagedResponse<>();
    pagedResponse.setContent(historyList);
    pagedResponse.setPage(0);
    pagedResponse.setSize(10);
    pagedResponse.setTotalElements(2);

    // Mockeo
    when(consultService.getHistory(PageRequest.of(0, 10)))
        .thenReturn(Mono.just(pagedResponse));

    // Prueba del endpoint
    webTestClient.get()
        .uri("/api/history?page=0&size=10")
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.content.length()").isEqualTo(2)
        .jsonPath("$.page").isEqualTo(0)
        .jsonPath("$.size").isEqualTo(10)
        .jsonPath("$.totalElements").isEqualTo(2)
        .jsonPath("$.content[0].endPoint").isEqualTo("/api/sum")
        .jsonPath("$.content[0].answer").isEqualTo(3.00)
        .jsonPath("$.content[1].answer").isEqualTo(10.00);
  }
}