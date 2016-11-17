package com.davidmiguel.gobees.addeditapiary;

import com.davidmiguel.gobees.data.model.Apiary;
import com.davidmiguel.gobees.data.source.ApiariesDataSource;
import com.davidmiguel.gobees.data.source.cache.ApiariesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of AddEditApiaryPresenter.
 */
public class AddEditApiaryPresenterTest {

    @Mock
    private ApiariesRepository apiariesRepository;

    @Mock
    private AddEditApiaryContract.View addeditapiaryView;

    /**
     * Mockito API to capture argument values and use them to perform further
     * actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<ApiariesDataSource.GetApiaryCallback> getApiaryCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<ApiariesDataSource.TaskCallback> taskCallbackArgumentCaptor;

    private AddEditApiaryPresenter addEditApiaryPresenter;

    @Before
    public void setupMocksAndView() {
        // To inject the mocks in the test the initMocks method needs to be called
        MockitoAnnotations.initMocks(this);

        // The presenter wont't update the view unless it's active.
        when(addeditapiaryView.isActive()).thenReturn(true);
    }

    @Test
    public void saveNewApiaryToRepository_showsSuccessMessage() {
        // Get a reference to the class under test
        addEditApiaryPresenter =
                new AddEditApiaryPresenter(apiariesRepository, addeditapiaryView, -1);
        // When the presenter is asked to save an apiary
        addEditApiaryPresenter.saveApiary("Apiary 1", "Some notes about it....");
        // Then an apiary is saved in the repository
        verify(apiariesRepository)
                .saveApiary(any(Apiary.class), taskCallbackArgumentCaptor.capture());
        taskCallbackArgumentCaptor.getValue().onSuccess();
        // And the view updated
        verify(addeditapiaryView).showApiariesList();
    }

    @Test
    public void saveEmptyApiary_showsErrorUi() {
        // Get a reference to the class under test
        addEditApiaryPresenter =
                new AddEditApiaryPresenter(apiariesRepository, addeditapiaryView, -1);
        // When the presenter is asked to save an empty apiary
        addEditApiaryPresenter.saveApiary("", "");
        // Then an empty not error is shown in the UI
        verify(addeditapiaryView).showEmptyApiaryError();
    }

    @Test
    public void saveExistingApiaryToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test for apiary with id=1
        addEditApiaryPresenter =
                new AddEditApiaryPresenter(apiariesRepository, addeditapiaryView, 1);
        // When the presenter is asked to save an apiary
        addEditApiaryPresenter.saveApiary("Apiary 1", "Some more notes about it....");
        // Then an apiary is saved in the repository
        verify(apiariesRepository)
                .saveApiary(any(Apiary.class), taskCallbackArgumentCaptor.capture());
        taskCallbackArgumentCaptor.getValue().onSuccess();
        // And the view updated
        verify(addeditapiaryView).showApiariesList();
    }

    @Test
    public void populateApiary_callsRepoAndUpdatesView() {
        Apiary testApiary = new Apiary(1, "Apiary 1", null, null, "Some notes...");
        // Get a reference to the class under test
        addEditApiaryPresenter = new AddEditApiaryPresenter(
                apiariesRepository, addeditapiaryView, testApiary.getId());
        // When the presenter is asked to populate an existing apiary
        addEditApiaryPresenter.populateApiary();
        // Then the apiaries repository is queried and the view updated
        verify(apiariesRepository).getApiary(eq(testApiary.getId()),
                getApiaryCallbackArgumentCaptor.capture());
        // Simulate callback
        getApiaryCallbackArgumentCaptor.getValue().onApiaryLoaded(testApiary);
        // Verify UI has been updated
        verify(addeditapiaryView).setName(testApiary.getName());
        verify(addeditapiaryView).setNotes(testApiary.getNotes());
    }
}