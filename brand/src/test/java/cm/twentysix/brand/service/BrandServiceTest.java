package cm.twentysix.brand.service;

import cm.twentysix.brand.controller.dto.CreateBrandForm;
import cm.twentysix.brand.controller.dto.UpdateBrandForm;
import cm.twentysix.brand.domain.model.Brand;
import cm.twentysix.brand.domain.repository.BrandRepository;
import cm.twentysix.brand.exception.BrandException;
import cm.twentysix.brand.exception.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;
    @InjectMocks
    private BrandService brandService;

    @Test
    @DisplayName("브랜드 생성 성공")
    void createBrand_success() {
        //given
        CreateBrandForm form = new CreateBrandForm("아이캔더", "주식회사 캔더스", "000-00-00000", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        MultipartFile file = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        given(brandRepository.countByUserId(anyLong())).willReturn(4L);
        //when
        brandService.createBrand(1L, file, form);
        //then
        ArgumentCaptor<Brand> brandCaptor = ArgumentCaptor.forClass(Brand.class);
        verify(brandRepository, times(1)).save(brandCaptor.capture());
        Brand saved = brandCaptor.getValue();
        assertEquals(saved.getName(), form.name());
        assertEquals(saved.getLegalName(), form.legalName());
        assertEquals(saved.getRegistrationNumber(), form.registrationNumber());
        assertEquals(saved.getDeliveryFee(), form.deliveryFee());
        assertEquals(saved.getFreeDeliveryInfimum(), form.freeDeliveryInfimum());
        assertEquals(saved.getIntroduction(), form.introduction());
    }

    @Test
    @DisplayName("브랜드 생성 살패_BRAND_LIMIT_OVER")
    void createBrand_fail_BRAND_LIMIT_OVER() {
        //given
        CreateBrandForm form = new CreateBrandForm("아이캔더", "주식회사 캔더스", "000-00-00000", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        MultipartFile file = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        given(brandRepository.countByUserId(anyLong())).willReturn(5L);
        //when
        BrandException e = assertThrows(BrandException.class, () -> brandService.createBrand(1L, file, form));
        //then
        assertEquals(e.getError(), Error.BRAND_LIMIT_OVER);
    }

    @Test
    @DisplayName("브랜드 수정 성공")
    void updateBrand_success() {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        MultipartFile file = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        Brand brand = new Brand("송송이", "주식회사 송송이", "", "돈대신사슴고기조요", "123-12-12345", 3000, 5000000, 1L);
        given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));
        //when
        brandService.updateBrand(1L, file, form, 1L);
        //then
        assertEquals(brand.getName(), form.name());
        assertEquals(brand.getLegalName(), form.legalName());
        assertEquals(brand.getDeliveryFee(), form.deliveryFee());
        assertEquals(brand.getFreeDeliveryInfimum(), form.freeDeliveryInfimum());
        assertEquals(brand.getIntroduction(), form.introduction());
    }

    @Test
    @DisplayName("브랜드 수정 실패_BRAND_NOT_FOUND")
    void updateBrand_fail_BRAND_NOT_FOUND() {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        MultipartFile file = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        given(brandRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        BrandException e = assertThrows(BrandException.class, () -> brandService.updateBrand(1L, file, form, 1L));
        //then
        assertEquals(e.getError(), Error.BRAND_NOT_FOUND);
    }

    @Test
    @DisplayName("브랜드 수정 실패_NOT_BRAND_OWNER")
    void updateBrand_fail_NOT_BRAND_OWNER() {
        //given
        UpdateBrandForm form = new UpdateBrandForm("아이캔더", "주식회사 캔더스", 3500, 50000, "우리와 함께 사는 반려동물이 언제 어디서든 더욱 편안하고, 안전하며, 행복하게 살도록 하는 것을 목표로 하는 브랜드입니다");
        MultipartFile file = new MockMultipartFile("abc.jpg", "abcd".getBytes());
        Brand brand = new Brand("송송이", "주식회사 송송이", "", "돈대신사슴고기조요", "123-12-12345", 3000, 5000000, 1234L);
        given(brandRepository.findById(anyLong())).willReturn(Optional.of(brand));
        //when
        BrandException e = assertThrows(BrandException.class, () -> brandService.updateBrand(1L, file, form, 1L));
        //then
        assertEquals(e.getError(), Error.NOT_BRAND_OWNER);
    }

}