package com.mari05lim.tandera.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.common.base.Preconditions;
import com.mari05lim.tandera.MoviesApp;
import com.mari05lim.tandera.mvp.views.PersonView;
import com.mari05lim.tandera.model.entities.PersonWrapper;
import com.mari05lim.tandera.model.state.BaseState;
import com.mari05lim.tandera.model.state.MoviesState;
import com.mari05lim.tandera.model.tasks.BaseRunnable;
import com.mari05lim.tandera.model.tasks.FetchPersonRunnable;
import com.squareup.otto.Subscribe;

/**
 * DEV Mari05liM
 */
@InjectViewState
public class PersonPresenter extends MvpPresenter<PersonView> implements BaseDetailPresenter<PersonView> {

    private String mRequeStParameter;

    public static final String LOG_TAG = PersonPresenter.class.getSimpleName();

    public PersonPresenter() {
        super();
        MoviesApp.get().getState().registerForEvents(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MoviesApp.get().getState().unregisterForEvents(this);
        mRequeStParameter = null;
    }

    @Override
    public void attachUiByParameter(PersonView view, String requestedParameter) {
        final int callingId = getId(view);
        mRequeStParameter = requestedParameter;
        fetchPersonIfNeeded(callingId, requestedParameter);
        populateUi(view, requestedParameter);
    }

    @Override
    public String getUiTitle(String parameter) {
        final PersonWrapper person = MoviesApp.get().getState().getPerson(parameter);
        if (person != null) {
            return person.getName();
        }
        return null;
    }

    @Override
    public void populateUi(PersonView view, String parameter) {
        final PersonWrapper person = MoviesApp.get().getState().getPerson(parameter);
            if (person != null) {
                view.updateDisplayTitle(person.getName());
                view.setData(person);
            }
    }

    @Override
    public void refresh(PersonView view, String parameter) {
        //NTD
    }

    @Override
    public int getId(PersonView view) {
        return view.hashCode();
    }

    @Override
    public <BR> void executeNetworkTask(BaseRunnable<BR> task) {
        MoviesApp.get().inject(task);
        MoviesApp.get().getBackgroundExecutor().execute(task);
    }

    @Subscribe
    public void onPersonInfoChanged(MoviesState.PersonChangedEvent event) {
        Log.d(LOG_TAG, "On Person Changed info");
        populateUi(getViewState(), mRequeStParameter);
    }

    @Subscribe
    public void onNetworkError(BaseState.OnErrorEvent event) {
        if (null != event.error) {
            getViewState().showError(event.error);
        }
    }

    @Subscribe
    public void onLoadingProgressVisibilityChanged(BaseState.ShowLoadingProgressEvent event) {
            getViewState().showLoadingProgress(event.show);
    }

    /**
     * Fetch person information
     */
    private void fetchPersonIfNeeded(final int callingId, String id) {
        Preconditions.checkNotNull(id, "id cannot be null");

        PersonWrapper person = MoviesApp.get().getState().getPerson(id);
        if (person == null || !person.isFetchedCredits()) {
            fetchPerson(callingId, Integer.parseInt(id));
        }
    }

    private void fetchPerson(final int callingId, int id) {
        executeNetworkTask(new FetchPersonRunnable(callingId, id));
    }

}