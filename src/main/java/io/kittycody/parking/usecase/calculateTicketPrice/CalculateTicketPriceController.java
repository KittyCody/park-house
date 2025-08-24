package io.kittycody.parking.usecase.calculateTicketPrice;

import an.awesome.pipelinr.Pipeline;
import io.kittycody.parking.shared.controller.BaseController;
import io.kittycody.parking.shared.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CalculateTicketPriceController extends BaseController {

    private final Pipeline pipeline;

    public CalculateTicketPriceController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @PostMapping("/v1/tickets/price")
    ResponseEntity<Integer> calculatePrice(@RequestBody CalculateTicketPriceRequest request) {

        Result<Integer> result = pipeline.send(new CalculateTicketPriceCommand(request.ticketId()));

        return this.toResponseOrThrow(HttpStatus.OK, result);
    }

    private record CalculateTicketPriceRequest(UUID ticketId) {
    }
}
