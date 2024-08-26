package cm.twentysix.product.service;

import cm.twentysix.product.domain.model.Product;
import cm.twentysix.product.domain.repository.ProductRepository;
import cm.twentysix.product.exception.Error;
import cm.twentysix.product.exception.ProductException;
import cm.twentysix.product.messaging.MessageSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class ProductStockFacadeTest {
    @Autowired
    private ProductStockFacade productStockFacade;
    private NonLockProductStockFacade nonLockProductStockFacade;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStockService productStockService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private Product savedA;
    private Product savedB;
    private Map<String, Integer> productIdQuantityMap;

    @BeforeEach
    void setUp() {
        savedA = Product.builder()
                .name("MOCK_PRODUCT")
                .quantity(100)
                .orderingOpensAt(LocalDateTime.now()).build();
        savedB = Product.builder()
                .name("MOCK_PRODUCT2")
                .quantity(50)
                .orderingOpensAt(LocalDateTime.now()).build();
        productRepository.saveAll(List.of(savedA, savedB));
        productIdQuantityMap = Map.of(savedA.getId(), 1, savedB.getId(), 1);
        nonLockProductStockFacade = new NonLockProductStockFacade(productStockService, messageSender, applicationEventPublisher);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll(List.of(savedA, savedB));
    }

    @Test
    void handleOrder_distributedLockAppliedTest_100ConcurrentUsers() throws InterruptedException {
        //given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    productStockFacade.handleOrder(productIdQuantityMap, "orderid");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Product persistProductA = productRepository.findById(savedA.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        Product persistProductB = productRepository.findById(savedB.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        System.out.println("final quantity productA : " + persistProductA.getQuantity());
        System.out.println("final quantity productB : " + persistProductB.getQuantity());
        assertEquals(50, persistProductA.getQuantity());
        assertEquals(0, persistProductB.getQuantity());
    }

    @Test
    void handleOrder_distributedLockNotAppliedTest_100ConcurrentUsers() throws InterruptedException {
        //given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    nonLockProductStockFacade.handleOrder(productIdQuantityMap, "orderid");
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Product persistProductA = productRepository.findById(savedA.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        Product persistProductB = productRepository.findById(savedB.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        System.out.println("final quantity productA : " + persistProductA.getQuantity());
        System.out.println("final quantity productB : " + persistProductB.getQuantity());
        assertNotEquals(50, persistProductA.getQuantity());
        assertNotEquals(0, persistProductB.getQuantity());

    }

    @Test
    void rollbackOrder_distributedLockAppliedTest_100ConcurrentUsers() throws InterruptedException {
        //given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    productStockFacade.rollbackOrder(productIdQuantityMap);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Product persistProductA = productRepository.findById(savedA.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        Product persistProductB = productRepository.findById(savedB.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        System.out.println("final quantity productA : " + persistProductA.getQuantity());
        System.out.println("final quantity productB : " + persistProductB.getQuantity());
        assertEquals(200, persistProductA.getQuantity());
        assertEquals(150, persistProductB.getQuantity());
    }

    @Test
    void rollbackOrder_distributedLockNotAppliedTest_100ConcurrentUsers() throws InterruptedException {
        //given
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    nonLockProductStockFacade.rollbackOrder(productIdQuantityMap);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //then
        Product persistProductA = productRepository.findById(savedA.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        Product persistProductB = productRepository.findById(savedB.getId())
                .orElseThrow(() -> new ProductException(Error.PRODUCT_NOT_FOUND));

        System.out.println("final quantity productA : " + persistProductA.getQuantity());
        System.out.println("final quantity productB : " + persistProductB.getQuantity());
        assertNotEquals(200, persistProductA.getQuantity());
        assertNotEquals(150, persistProductB.getQuantity());

    }

}