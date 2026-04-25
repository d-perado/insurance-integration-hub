package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.ExternalOrganization;
import com.insurance.integrationhub.dto.organizations.CreateOrganizationRequest;
import com.insurance.integrationhub.dto.organizations.OrganizationResponse;
import com.insurance.integrationhub.dto.organizations.UpdateOrganizationRequest;
import com.insurance.integrationhub.repository.ExternalOrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalOrganizationServiceTest {

    private final ExternalOrganizationRepository externalOrganizationRepository =
            mock(ExternalOrganizationRepository.class);

    private final ExternalOrganizationService externalOrganizationService =
            new ExternalOrganizationService(externalOrganizationRepository);

    @Test
    @DisplayName("외부 기관을 등록한다")
    void createOrganization() {
        when(externalOrganizationRepository.existsByName("국민은행"))
                .thenReturn(false);

        when(externalOrganizationRepository.save(any(ExternalOrganization.class)))
                .thenReturn(createOrganizationEntity());

        OrganizationResponse result =
                externalOrganizationService.createOrganization(createRequest());

        assertThat(result.name()).isEqualTo("국민은행");
        assertThat(result.managerName()).isEqualTo("홍길동");
        assertThat(result.managerEmail()).isEqualTo("manager@kb.com");
    }

    @Test
    @DisplayName("중복된 기관명으로 외부 기관 등록 시 예외가 발생한다")
    void createOrganization_duplicateName() {
        when(externalOrganizationRepository.existsByName("국민은행"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                externalOrganizationService.createOrganization(createRequest())
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.ORGANIZATION_NAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("외부 기관 목록을 조회한다")
    void getOrganizations() {
        when(externalOrganizationRepository.findAll())
                .thenReturn(List.of(
                        createOrganizationEntity(),
                        createAnotherOrganizationEntity()
                ));

        List<OrganizationResponse> result =
                externalOrganizationService.getOrganizations();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("국민은행");
        assertThat(result.get(1).name()).isEqualTo("신한은행");
    }

    @Test
    @DisplayName("외부 기관 상세를 조회한다")
    void getOrganization() {
        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(createOrganizationEntity()));

        OrganizationResponse result =
                externalOrganizationService.getOrganization(1L);

        assertThat(result.name()).isEqualTo("국민은행");
        assertThat(result.managerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("존재하지 않는 외부 기관 조회 시 예외가 발생한다")
    void getOrganization_notFound() {
        when(externalOrganizationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                externalOrganizationService.getOrganization(999L)
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.ORGANIZATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("외부 기관 정보를 수정한다")
    void updateOrganization() {
        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(createOrganizationEntity()));

        when(externalOrganizationRepository.existsByName("국민카드"))
                .thenReturn(false);

        OrganizationResponse result =
                externalOrganizationService.updateOrganization(
                        1L,
                        updateRequest()
                );

        assertThat(result.name()).isEqualTo("국민카드");
        assertThat(result.managerName()).isEqualTo("이영희");
        assertThat(result.managerEmail()).isEqualTo("manager@kbcard.com");
    }

    @Test
    @DisplayName("중복된 기관명으로 외부 기관 수정 시 예외가 발생한다")
    void updateOrganization_duplicateName() {
        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(createOrganizationEntity()));

        when(externalOrganizationRepository.existsByName("신한은행"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                externalOrganizationService.updateOrganization(
                        1L,
                        duplicateUpdateRequest()
                )
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.ORGANIZATION_NAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("기존 기관명과 동일한 이름으로 수정할 수 있다")
    void updateOrganization_sameName() {
        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(createOrganizationEntity()));

        OrganizationResponse result =
                externalOrganizationService.updateOrganization(
                        1L,
                        sameNameUpdateRequest()
                );

        assertThat(result.name()).isEqualTo("국민은행");
        assertThat(result.managerName()).isEqualTo("이영희");
        assertThat(result.managerEmail()).isEqualTo("manager2@kb.com");

        verify(externalOrganizationRepository, never())
                .existsByName("국민은행");
    }

    @Test
    @DisplayName("외부 기관을 삭제한다")
    void deleteOrganization() {
        ExternalOrganization organization = createOrganizationEntity();

        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        externalOrganizationService.deleteOrganization(1L);

        verify(externalOrganizationRepository).delete(organization);
    }

    @Test
    @DisplayName("존재하지 않는 외부 기관 삭제 시 예외가 발생한다")
    void deleteOrganization_notFound() {
        when(externalOrganizationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                externalOrganizationService.deleteOrganization(999L)
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.ORGANIZATION_NOT_FOUND.getMessage());
    }

    private ExternalOrganization createOrganizationEntity() {
        return new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );
    }

    private ExternalOrganization createAnotherOrganizationEntity() {
        return new ExternalOrganization(
                "신한은행",
                "김철수",
                "manager@shinhan.com"
        );
    }

    private CreateOrganizationRequest createRequest() {
        return new CreateOrganizationRequest(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );
    }

    private UpdateOrganizationRequest updateRequest() {
        return new UpdateOrganizationRequest(
                "국민카드",
                "이영희",
                "manager@kbcard.com"
        );
    }

    private UpdateOrganizationRequest duplicateUpdateRequest() {
        return new UpdateOrganizationRequest(
                "신한은행",
                "이영희",
                "manager@shinhan.com"
        );
    }

    private UpdateOrganizationRequest sameNameUpdateRequest() {
        return new UpdateOrganizationRequest(
                "국민은행",
                "이영희",
                "manager2@kb.com"
        );
    }
}