package cm.twentysix.user.messaging;

import cm.twentysix.user.dto.AddressSaveEvent;
import cm.twentysix.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final AddressService addressService;

    @Bean(name = "address")
    public Consumer<AddressSaveEvent> addressSaveEventConsumer() {
        return addressSaveEvent -> addressService.saveAddress(addressSaveEvent);
    }
}
