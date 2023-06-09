package com.ascien.app.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ascien.app.Adapters.WishlistAdapter;
import com.ascien.app.JSONSchemas.StatusSchema;
import com.ascien.app.Models.MyCourse;
import com.ascien.app.Models.TopCourse;
import com.ascien.app.Models.WishListCourse;
import com.ascien.app.Network.Api;
import com.ascien.app.Network.ApiClient;
import com.ascien.app.R;
import com.ascien.app.Utils.Helpers;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WishlistFragment extends Fragment implements WishlistAdapter.RemoveItemFromWishList, SwipeRefreshLayout.OnRefreshListener , WishlistAdapter.OnItemClickListener {
    private static final String TAG = "WishlistFragment";
    GridView myWishlistGridLayout;
    private ProgressBar progressBar;
    TextView myCoursesLabel;
    RelativeLayout myWishlistView, signInPlaceholder, mEmptyContentArea;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WishListCourse wishListCourse;

    WishlistAdapter.RemoveItemFromWishList mRemoveFromWishlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.wishlist_fragment, container, false);
        init(view);
        initSwipeRefreshLayout(view);
        initProgressBar(view);
        getMyWishList();
        return view;
    }

    private void init(View view) {
        myWishlistGridLayout = view.findViewById(R.id.myCoursesGridLayout);
        myCoursesLabel = view.findViewById(R.id.myCoursesLabel);
        myWishlistView = view.findViewById(R.id.myWishlistView);
        mEmptyContentArea = view.findViewById(R.id.emptyContentArea);
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.googleAccentColor1,
                R.color.googleAccentColor2,
                R.color.googleAccentColor3,
                R.color.googleAccentColor4);
    }

    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Sprite circularLoading = new Circle();
        progressBar.setIndeterminateDrawable(circularLoading);
    }

    private void initMyWishlistGridView(ArrayList<WishListCourse> mWishList) {
        WishlistAdapter adapter = new WishlistAdapter(getActivity(), mWishList, this, wishListCourse,this);
        myWishlistGridLayout.invalidateViews();
        myWishlistGridLayout.setAdapter(adapter);
    }

    public void getMyWishList() {
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<WishListCourse> mMyCourse = new ArrayList<>();
        mMyCourse.add(new WishListCourse(0, 2, 4, "150", "1.56",new TopCourse(1,1,"Криптовалюты",1,"as","as","as","as","data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAoHCBQUFBcVFBQYFxcaGhsYGxcXGhocIh0bGxsaGxsaGxgdICwkHB0sIBoaJTYlKS4wMzMzGyI5PjkyPSwyMzABCwsLEA4QHhISHjQqIikyMjI0MjIyMjA0MjIyMjIyMjIyNTIwMjQyMjQyMjIyMjQyMjIyMjIyMjIyMjIyMjIyMv/AABEIALEBHAMBIgACEQEDEQH/xAAcAAABBQEBAQAAAAAAAAAAAAAFAAIDBAYBBwj/xABCEAACAQIEAwUFBQQJBAMAAAABAgMAEQQSITEFQVEGEyJhcSMygZGhQlKxwdEzYnKCFBVDkqKy4fDxBxY0U0Rzk//EABkBAAMBAQEAAAAAAAAAAAAAAAABAgMEBf/EAC4RAAICAgICAQIEBQUAAAAAAAABAhEDIRIxBFFBE2EiMnGBkaGxwdEFIzM0cv/aAAwDAQACEQMRAD8A2M2KC86E43jKrzrAcU7Xs1wl/U1m8VxSST3mPoK2ozt/BueKdrkW4zXPQVk8f2mkk93TzNBAhNSLHTUWx/qNlmkkN2Yn1NNWKrCp0FdZCNxWih7FyREEqVISdhSjax1ogMSoWrjFESkwYwI3o9wbjZiAFr0Ojwkkx9nGz/wg/U7VfTsxiOaW/iI/KlJRaqXQKT+OyHjPEWnNCu7tvWhTs1NtdB8T+lFcF2Sa4JsT1vUucIKkdHj+Nkzyq6+7IOEShEttRzMpQ1zGcIMUdzbSh+JukYblXJLIb5/FeJ03ZLg8UEJubeQqzLxYfZA+JrMPKWqREcDMBp1rNtszQbm4w4GhHyqlJxhm31oahYnXXyqQ4SRjcIbelvxpJBZbXHK3vUf4LIEGaNreVZWfAugubel71b4K8kbEjbmKfW0Hemb/AAnH0bRjY0ZixKtsb15TiUdi0guv0qTAcfkj0YkitIzTIcWj1cEGu2rI8N7UI9gTrWhw2ORxoQaskuWpWriuDT6AG2pV21K1AHKVdtStTA5SrtqVqAOWpWrtq7agBpFctUlqbagD5sCE1IqWpwNKuhRSMuTFSrldAphZYwkqrvVwyCTwotyeQ/3pVDD4RnYKOdaWBEhWyjX6ms8uZQX3HGHJ2ScE7JLIc0zkD7ifm1E8dw/AweFEXN5+I/WiPZjGCVShXUb2/WpeN8Mv7kfyrzZ+TKUqbO2OCKjyRS4FJJfLGlx8ABRDF4uQGxUD43pnAs0AJkjIHU1BJxEPPfUjkAPpUPLIFBEEuOkU3Kj5VLBxB5PcW5HQG3zo7PhI2AMg9F/X9Kdh8JmFrAAbWFvpUryGkNwp6YExCTSLldwoPq1T4bhiCPu5GLDlpatB/VwttVYSw5ilwGGmtRLyJSY69syGI7OLfwObcrj6U1OEyxmxyuvy+la10Vmyrr5jYVRxqGM76HmKqOWRDigCxcAhY1W3lb8qlgwLuLvJYdEFz9f0oqZAwvYHqKKYLAEhZIr68gBp8at5a+BxhyBsXZ92t3cZPVpdPx1+Qq1D2QyNmkcW+6NB9d60cbPApkmYAAe7uay/E+1QkY5RoNrn8qOcpdBUY9hDHcLjKFVAtblr+FYziXAXJBjjaxNtjV+ftORp3nwXT8KHSdpL9T8/1q0miHJMo8V7PyxqGUPfqoNwfhQjA9pp4WyyA6aa3BrSf9wg6Nf/ABfrU6YdplJEbEWuc4BFvjWkMjitoiUU+izwbtrG9gzfA6GthgeLRyDwsPSvOMBhcGjkyRJY87XA+HKtRhI4wLwAHyUVupRaIpo2CuDT7VmZeKulgUtSw/aUA2ZSKYGmtStVbDY5XF9qtjWgBtqVqdauUwOUq7alQAqVqVKgD5upyoTXQtTI9q60jnsS4e29OLKNqazE05MI5t4TY86ozlNL8zCPAvE7ddLVr4OyjyAMZF15DlWYwUaQEMTe+9HDHKVEkMjMvQH8q8nzFJSu9HV4mWOROldG74LwBcOnh15k9aIyJnGi60I7O9oo3jEcrZHAsc2laNIRkPduDeuKOJvZ6CyJaKbYZO7KuBWexUccekagkVdxseJF72IoK8Z7sud7n9Kb1oU/Zw4gsdd+lTIZ81lND8FKoYX1v/u1WMZxuSDMDHYt7pqWndIzv2EsRxA4cBpZL6e7QmHBnGOZnORTooXc+Z/SpuHcPWQCSVhI78twvoPzpnEsLJhrPG3gPLpTSS0uwft9FfGzyYM5Cc0ZOp526XqLGcRElsgsvIUyKNsW4EjmwOw0+v6URxXDY4x4badeVXpd9k7BmHchrHY+daTgPGxh8yt7u4oHhoLsWJ2G52+FD8ewMmTNrlJAotWNJj+0naJ8XIxuViXZevrWZlnLabDpUig5GHQ61oOyvZwzNnkQ5Ft4WBGb9RXTaSM+zP4XASSAmONmA3IFWOHcNeVrIL9fL1r0qcvAjiNEVANrD8qwvCuJSxyFshIdrtlQ/TSp5NrQ2qNhwTsxHGozgMd7nlRDjuF9iUjIDEWABtQrDdosPmMfeNrux0senlWO41iGSUhJ2cDZr9eVTFNvY20lombsvjETOVuOYBv8arYfFSYYpIDodct7/wDFEMIcdKvdiQqAuYZja4PmazyAksGN7A1r2Sbb+uhIAxQEHy5+t6s4buzrl16DX6Gs7wCHND4r2zWFt7+Qrf8AZp2iQtIoyn3dNfiKlRS+RtszmKkYSAK2Ucxt9KOpjTGFu+a9P4ljsJiXyspWQXXMOnrtb1rM8Si7pg0eZ7e8oBNh94dPSt45K0zJx+UbqLEBhepQwNea8Y7SNAIpEbMpNiPh/pRXg/bSKWwZrHz0NaVe0Tyrs2tKqWHxisLqwNWBIKCiWu00GnUAfOVKlSrrOUOcPlhVc1telRY3i19EXShFKg5l4keXKTbOySM25q1wri8sDWQkjpv9KlwfC3kAbSx/DrReHBxxbsCT6VGSMZRqStFryIwfGD36CScTR0zSxfEUV4PxjDpos0kY6XuPresVxTHOQVVfDQAOw6iuJ+DF/lk0dmDysrX+7FWevcT42jEZcSxHO1v0p3CsTHJGy5r9Dz868iGIbqa03ZjHyYdrupyHXzHnWOXw3BXys7YZPqOlE2eGwvdyDNoL3/SieIw4YkkCRjoMx0A6+nlXcHxCHEJcEMegNj8qkyhRmsR5HkPM1wNux1QMfs86ANDIwb7VrWHoOld/qCeS3fT+Hpb9TRmHFAaja17/AFpuLxAkQWQvnGoVSSB52o+o/kKQMk4dGoCxnxDazb+pqpiMCFNmYHS7a7UWi4Q76Iix/dLbjzsOdH8DwWDD2eVg78i9rA/ur1+Zq4vl8ikq+DJLgmRO9kusSjwIdC3mRyH41hcVKWxBmIsQfDfp0r0/j+eVSxU5B7q82P5CvMuMQOJPHvvlGwFTUjaHFIvvgmcGeJQR9uMbjzA5ip8TxLFSopikLKgtlXRh6jnVHg3aT+jvkdMyeW49K1a4PCYoCSNir/eiNmv5jnVwzOP5l+5OTFv8Jkj2ixVspkuPMUS4L2tkjZRKqsl9TYXtRLE8HkJysYZ+negxv/8Aou/zqjJ2f11wco5eymRx/iua3WWEvRi4SXspYzAjESTSxn2d8wa1hryJ2BvViF4sL3YkivJlNxpqG2JYaEb6b04cHCgr3GNsbErmjAPS+lqL8M7NyObx4JL/AHsTLn/wLoflVqUXq/6EtNbozs+KnxNljUhELWIuAFJ2d/IVFheFtI3dQjOSfG429Aelelr2PZwP6XiLoP7KFRGg8upFGcBgIolyQII1G7W3HqdzRKXHS7FFXsxmJ4OsSQxKHurZi0dr3setS4Picbr7YEgMyi58VttQNK0nHMIslnQBygsAp1Fz7y252rG4wDMVKKjFtcy6nWwIBFxtvQlXY7T6NBBxHBJHcWUW3bUmmcVxalIjCgLMcoFtCpGtLh2Cwv8ARzGR4jckg+78f1rOy8UGQ924dY2KIL3Oa1s3nvam16FYJ4VwePEGSOeNjlYgBfsm5FD+K9hXQlsNJntr3beFh6da3HZ7hDwxGWQHO5LeeuutWpJ1LBJgR92RdCp5XPSurGnRjJnkeD47icM2Ri110KvuK2nB+3kbWWXwnqf1q32l7PLMe6lyiYqWimXaQDkfPqK8qxMDxuyOLMpKkeYq/wBRL7HvuB4pHIAUcHyvV/v/ACr54wXEpIjeNyPK/wCVabDdvJwoDAE9b0qQ7fyZmlRvhvZ2SaMSBgFJNrjobXqlxbhj4eTJJa9ri1bRzQlLinv0ZcJJXWijSpUhWhJPBi3TRWIHSmPIxNyTemk1yglQinaWy3Djiu+tE8GEk1yaA6/8ULwE6o12UMNN+Q52qxiMVGrMYgdee1j5Cspxt6R3YuKjyk/2+Q1LhsKFzNsfmKD4viNhlj1XbXpQ53Lbm9ORb6UliS7dly8xvUFX6dsbDjJENwa0/Be10ynKWf53/Gs2mAkZgoG/M7dd6LR8JSKwdrPbML+6bchUZsEJx6OKXlrHNJvfo3XDu1xEeaUyZQbeFBv0JFOn7ewhSUjla2+lretC+FcbgK+0Xu2IylgLqw/fX8xRTD9nYpgWR0sTspuP9+RrwcmFQf4r/sevjycloB4rtzI5BjUR676k/OvQ+C4zC4hFlcHOtic7EhTbca2oBF2DRmGqEc9wfmNDRD+o2wgIUgq22u9vwpQqP4ooJ703sI8Z7SRICirdSNHt4TfoetedYvFXk1YDXZ1BB+NaHHxqql0vcgh1XQkc/ARZqo4TCHEoogj0TKD4DHcX1LM10LWvzFdkJKas52nDsDYngckgZo8OGB2IFtfI7WqnHwWSMhmkTDsOfei/91Qa9Cw/BzEuWSdUXS8YsbXPXw/QGqsvB8FmLMpYgga9bFvdydBz8qjg4ujVZLRmYeKSkhBKMSb2t3Zvb+Ian5URbic8QGbCn+EML+pB9341oMTLFFGQoZFtYiEZTuF+yPO/prUWE4Vh42JAaXPukj5wxW12ynLmIzAXJO4rN4YPbRSyNdAkcfjYXMRA2LlvCD0vbWtZ2exGQXN8p1DL4tP0rMdqMM+IQRxKixjXu00Zj0GawPot6zuB4lNhTkjZhbeOTT6H8jWPBwfJGqrIqPaJcdDluWzW5AFj/dAv9KxvGO1cmYosTBL6EMqkjoVe1VuA45sWfaXjcdNAT68qP4vB2QftDbdAczi/3hewHnrXZCcpq6OWUIxdWZ3DdrZFQqsL5idJM0OYDpq1v+at4fCpNIJnXvZCFv3kuaw6BIlNyPlU8XC2O5kseWW+lvvc6KYWARqGLOEFt3CL6ZiwFbRlJ6SMpxildlKXs1LMLSP7PlEi9ylv3gCzufI5b0SwHZiCEhiodl90ZQqr/Cn5kk+dJ+1mEjFjInohZ/qFt9aqT9tMMQQrML88jGung38GCaXyW8dirGwsSOVAuKRl1LEfKpBxbD6HvV8V7GRZEB65WK2Pzp8hd1JjAkW3vRsr2Hotz9K0SohuzA8Yx8qyIpkJVLMin7O4/Khv/UOFe8hmUW76PMfVba/X6VtYZIJFsojaXX2cvvlr2CqmS4Ftb30qj2n4OjTQLICyxxqioN5JGO3+G/oaJS47Y4bejzvhXAMRij7OMkffOij4mtXh/wDp3HlHe8Rhjfmm9vjmH4VoUBBWMD2qEFowLRRj7pA/aG3X6UTmngv7bLm9QNPIdN6xebfRo4N/J5fBx6aLMsLlIydE3tfp0odicS8jZnYsepqGnxRM5yqCSeQruWOKk5JbZzW6qxlTR4V2BZUJA3IB09aIRcGfJnZgu5sRtbkTyNXoe1LpEI0jRWGhZQPEB94c/WifKKVK/wBwi0+2ZwilUkrlmLHc9KZaqAVKlXaBnUQkgDc6UcwvAwBeQm17XQghfNj60DvU4xkgTuwxCncdfWgwzQySSUHXsPRcQTDh4yVlIIK3Gm3XrQmf2puW22HTyFD66CRtUuPovFijF8pbfsM4eHw5SdeVSwySRm6MwPVSQfpvVDAQyyEZbhSbZyNBR/DyiBVZ2F7kZmBHyX05fWuXLFdPbPXwYJZFy/LH2w1w3jWLVAVkBJBsJAL6eYofje1uNLKhMTHZbDNv8aB4ntG50QZQCdb6kfl+NaHs5w4xqJpE9rIQkanXKW1LkHcga265R9quZ+PGCto0zZsbqMNtfJp+CQSkF8XIpNrmNVCqgOozsNSbEHKCNCLkAi5mBMRKzouSKADKhXNmbXdCLBRuPCB6nmBjxJClJESyPYgSZyx967WH71zfe++4NnFcSLKX97Kh8FyCCFIuCQVO494G1vKsVKulRztXtml/qaIHPkDPlClmAYlQSQPO19L3qOVMm1gBtyt11rzzB9p5c4XDRBWbkXY3sNyAUTYX1FqvHD8TltI0kcYNrNdL+LbKY1ZjflY05LkhRdB7EcYjN1jzStr4Yhm23udh8TyquMRn8TRhWX7LNGxAvvZGJAPna9RHDK+GaKVvdBVmQNckas+QgXN/ibg63Wh0PDcPhS3dyEyEBLSSRqTfXwxgA3uALGocEkUp2yXiOHaQLklyFc5yuRkYsN3styb663Glra0+XDsIFkkPeKfC7IDmje17AMSSB0Pw3CkYJy7qupuQLDS9zt61PdZGaKSSVEViGJfIe7FwVKm6nKSWGuhBI1ojT1IG2tot8IwkiAsJFce8jCwDofTYitCvEHDgjXS1zow/UVlsM3dSywBsy+KSNrjcWLe7pqpU+pNLtDxUtDGAMpkXXr3YJAH8x19NKMOOSyaeux5Jpxt9hifi8jZ+5N0FmeVgWVQbm6R28egJzWy6bAa0Dx/EowrZ5HlkJv4wHDJqF84xcNoCjISNG3AaLikke2rBcqPdgydACCMyjkrXAsPjzgfEDHOC5urnK5JvcsdGJO5ufqa9HJP6cbSOOMOctshxLoXJjDBL+EMQSB5kVXYmvQJ+GQtq0anzAsfmNar/APbOGk9wup/dbN9GBrhh/qEZaaaOmXjNfJl041II0ieOOSNAwUMGBGa4OqsOrfM1ew/GMPJIXkjaJjYd7E5BWwbWyAXNyBtoAN6vYrsVIBdJB6SDL9bn8KzWP4e8MjRvYsoBOU3GoBHLzHzrsx54z0mYSxV2bPAY3EBg693jFGxuveWuF0Ya35lWFgCBck1yRVnnbE52PdoxMRBSWOTlmj5gDS4rEYTEvG2aNrH4EEbWIOhGuxrTYSQ4kq8Rjhlj1uuVSRlCgIqxqMt9LMxtoNBodHBTX3M+Tg/sScCOWB5Tq7AuxOpJyhz+I+VY9IlnvLLOA7E3Bv8ATTat/hJRIZFKCOUD2kYHhblnS3Ig7bbagG9YLiHBpI3KgXXdT+6dq5JRcXTN4yTQI4bw9HGeSTIl8t/3t7En3bjarUuMihJWIFjltmB0v165h5GxoXiZ87MwAQMc2RLhR5AX2qCvX5V0cHFvsnxOLkk1dr2FvhUFKnxRlmCqLk6ADmaV2aJJDaKcI4JLiT7NfDcBn5LfYnnarGG4OqqXlZbBeR0VjbRwPF6gajoaqtju6kJwzsq2tueY8QF9SvrSnGXF8e/uKMk5fY5xbhT4d8rFWHJlIIIofT3csbk02lFNLfZRylTgK6yEb0wGVd4akRLCTfTLclV87sNvLlVOlSatUXCfCSdJ/Z9BdscIfBG3eAEFCbELfVhcEE6/A0LmmZzdiT8dBfoOQphpWpRgkaZM85qn0ul6DHZjBCSQuwukS5yDsWuFjU36sR8q3yoTNChcqe6zFxl95vGxGYFde7G99/Ssl2Ta0GJKmzBoTcbgXfX52+laR2V44JXUSKoEUitrcEgoTfzUpc/aPlXF5b2VhG4mZm7vxZiwvfXUsxvqQA3qNOlPmnaP2a6PpmZluynZkGtipFx8T0Bq9iMOhYGNbovjVQFA7s+IjSxBBudtiNdNKqcHmsC3dqTchWZibX0u1t7dddetcJvRnsNw+dJg8cUjBHzAhGtYG48VrWtWw4rHJZhhWeJyS7vlfKwFgcpVWAN2GiAXqpxKeSCQRxQd82UDMyu9suwVQLqNevyq3DhcTMEkkTupFzNogBLEMvjDeILlyhdTbW4OlU5BxKnA3kUuGlaeRtmXMxSwPumUrc66C2mvnWW43hWincWy3IkXQjfXYgW1vyG1aUdm8ZJpNitPu55Ht/JYKPSrUPYWMDxSO3koVB8zmo5IODKWGw2aMTKAxdbhSNBe+e+u4IsKiwcRXMRY+FwNCdSptoCN60WG4P3KZY1VND4mJc6m9ydBodQLb9L1CsQRtA1h71wgFwMyi5G+obcWsL6ZgItF8QGjyvikMsYjZe8GUclEasbnMbm7jXna9htVXiMbSNAqC+aGJVF7akbAk23P1olLBJJHLMAc0gMUZsfcJ8cm2gJNhfkFvsa5ieHP/RImt48Pdb6EFVNtORsV28jW+Ca5UZ5IfhszeMwrxtZxa4zCxVgQbi4Kkg6g/KqpWtfxTDnFwiZNWQXYWYmwBz3NsqgFbqL7NrqRfKFDXZ2c9UegdnscssKs51Ayt/EvP4ix+NTtG491j1y3Nh/LtfqepNYrgnFe4LArmV7aZstiDvfKdLXB0rT4firye62FF/vyuT/dshrycviyUm10d0MsWlfYYTGWtnFrdKB4DBjFHGOftt3aE8smqn/J8qJNh8Qy6vEo6phw3yaRm+dO4Vg+5QIrFhckk73PpWSf006e9extc31o84dCCQRYjQjoRoRU+CxTxurobMpuCPw9Dtbzop2twmScuB4ZBm/mGjj52P8ANQRa9nHk5RUl8nBONNpmz45MtocVESJLLn2tZluBpYW0ZbWGx00qnjsaqt/47SqwDo2VW8LDMVuzg6MW69b62FjHYZI8El47S2S5KZbX8Q8VvEbX5jc6XNNHDg8UBZRfuud9u8kI59DRlprYoqtHk9qtYHASTErGt8ozMSVVVXqzsQqj1NaPh/ZSwRpXRmdQY48xVJGa2VBMD4m1uRGDa2rCmY/jKx37tEjkA7iXCFFeJlUm7Ky6+8L6ktc3DV3JezmcvQBgwi973cjqNSMyspUtyGcG2UnTMNr0RxHEoFjyxR2JtmVwCNL6Mb+M9GFj58qoY/iTyqiFUSNL5I41soLe8dSSSbDUk7VStRddC432TYnEvIxZzcn8hYaelRUhXQKLLSo5UuGw7SMEQZmNzYdALkm+wtzohh+CSModyEUrmHM2sWDWGpWym9rkb2q1JxGOJVSNEawLWVicrkmx7y3j0IIIsy7ZjQo+yXL4RBw4xwOpniWWNwbMr+diVZGtcHl+G9P7Q8ZGIcnu03NnC5SRyDam+3rruaF4nFPIbyMWPwHrYDQdfOoKzcFy5fPRab40xU6lStVgKm06lagZqeyvFsxXDS5FiZGiDBQCGds6uzDVrOF32A9a2PCYHdZI5fG63jkjbfKdQLkgagXU7aNr4tPK8M1m/wB716X2e46j5O+9nIoCCX7yjZJRzG4D/wCprk8mHJG2J0T4SFIpA85dkS6pIScsbXv7WP7L6+9sd99Tqo481nBQ8w2h+Ib6/wCtVsXhgwDEkHLZZUI93kCfddPW9CJY5IPEocLvnw4BU3P24HJAOouVOt68pt3TO1dBPEOcwEYMhuA4Rlut7WdgSCND6kDY1OpAGlunL6Xvb4VluHTYdXeRZIi7e8ZXeI3vckd4G1vuQ1XMTiUcIFlEeVlb2WIjsQv2MucADztUtWyrNCj620v0vb6fpSxPFYokLu4sPyIU+EXawJAJAIF+VZqZ4u8WRpR4b3EmKQqwJJylAGuBfYU9O0EPuZxa1jkQsbaAgtJo17AarypqL+CQx/WCyoZL5Yw2TM4K+JWswUXu5ttud7WOlUJkQKoxMgiiOyuFR5Re5uijwJcknmxOtgcoqcQ4wQmfDqVkPhErnvHANxZbjKm2yjmK8y47i5e8ZJHdpP7RnuCb6hddSLV0YvHlNmWTMo6PZsLxLDzxs0DHIhdVktuyJqQp0sMwsDobfGiXDZI2RzlK3ZwVYblWy5v5gA3xvXmX/TniHgMRbKVkWUG+4ICyL/dsfga215Y5pD3R7tghY6WDWbO9xucqr8LeVavEoNpEfU5IH4/hsmDkaXDjNEbloxe6je4I1t5qQw22qjj+HxYlO9gZe83ePYm7WDWGi9SScthe5IY1rIMTkOYeOM2vtdT5HpubevpQ7E8JhlbvI2aKTXxpz6h49Pjbe+taRkvnslxdfY88ljIsSCAdjyI6g7EelRWrfvHi4kKyQJPFaxaJFJNrBS6EXJUCwGgHwrOYgYRpIwwaJbOZAEKEEL4QASwzFgdgBqBV2TQIhlZDdGKnqpIPzFEoO0GKTaZj/HaT/ODT4uFRuxInQRWuhZkDMc1ghTNdSRzOlWY8HgsuYyyXuLKzILho8wJKKxUBtCeVttRSaT7Q1ZT4jxqSdAkix6G4ZVKnaxGhy2PpyFX+C9nndg0qFUGwe65iQSoJGoGlzYXsNrai2kmBjIVITK4FiVu4ZrD72hBzMCQtvDsNDVjCcCxc9w5aGIn+0JZst82XXxOAdQG0G+9zQqSpaB929g91kxUiwrIXRbsXYABV3dmNlzWufEQCST1rS8OmSXvLaJG4jQW+wsaEfO5PxruP4Z/RsMwjsqaZifE0hOniOwHQDpWQlhmveNHKtrdRpfb8qxzpyjr2cr85ePmUWu1f8zC4fis8cZjjldY2Nyqm2+9jut+drX53qlTI5A23yp9epdiqhU5UvtV/gnCmxMndqwWylyTqbLa+VRqza6D8K0mB4XBBaSVihSQiOZZVHtAmZY50jZzFte6Em2htSsYHwHAwBHLimyQOSM6ENZrAoJGUMIw1xqQT+7zq92h4VBCgeNu6lXJlRWzrJckmSNs5dUAt4mADEaDpVm7TSZmkjjSKWQMsjR3yPdgyt3TXAcW97Xe+h1oATc366mmIt4niMj5gGKq5zFFJC5j7xA5AnUjb5CqldrlqG7GkkKu06OJmNlF+vkOpPIeZoxhuHJGjtMpuMqkG4yZicrgi4dTpYi9vuka0A3QNghAKtIj92TuAQGHOzEW2v1rRcVxOCWJI448wsxDkgSAk3CvZQrW1HPTLZhrQrifFA+dUFw+UNIb3cJYgldFBuPeCgkW0FzcVUTgpSTt6+9FRk6f3E1r6UqQFLLVgKjGBxtwFzZXGx5EdDVbhnCZJ2ARTa6gvlYhcxsCxANh5+Rp3F+DyYZ7ONNbMNQbGxsfXQ8xztWUpRcuN79FpSS5Vo0/DOOzYfQOVXcqRmQ8tUN7H0+daPAdp0fV4gerQvp/ca9uWxFecYTiOgWTW2x/WryIhIcWOu5ANc+TDGWmjeORrpm8x2NwMurq380Sk/MNf69aDnhXDZCNZhrsqW5n7znzrMyNJclZHF+QYkcuRvamxxyuReeUDTYgdRuB/u9RDxILq/wCJTzS+38jWrwXBC+SORl3zSOq8ugG2w3qRuJYCJskaxO98qiMBtSdC8rkhfFfn0sCay8PA0fIZ5ZShObxudiNxfncW+FbjhuFweHjYxIHQa2U95mLKFO/2trow26Vr9OETlnklegFisZLiQDGrKl7o6EKpsbFGdxZCCGuChuCNr1luPiONRCjd66eES8wh17vTRrMdDy1tpa244jhsXiiyRhYIjYnMczE2+6t7E32uBrWaxWCw+E1J72XWxa176gWUaKPma0U10iY4Wncn/EB8OxEmEkRiLBwL3GzA6H8j6npXr3BOMRzoC4Y5ctogL+LkG+9r7payjQnxC9eRNie+QxyDXMWU9L20FS8K4tJhnCO2Uj3X3BHRuq1M4OW/k1i0v0Pc5JEiUyTZVG2RRfUnbQZpHJ/0HMx9zHIodgYSdLMVDa6DMQSL7aa71neD8cjkcPIvtQtkDN4Rp/Zk6KW6nXbWicOCMjiTEENIPciGqpf7oPvNa93PnawrGky7o46MrnK7Acs1h52HL5ipWcMAJIxJcXIsH23uNbVLjJ44yFkMY0vlYXtSbiEPhIVpCwJvGrPtvt+FSo0U5WV5OF4O4zwRr/KV/C3n8jRDD9n8ELMIEIO3hJqFOMYVdXtHy9qrJ5faAuNqKjiCZAyHMCBly6gjytuKqmTaLOHwsceiRqn8KgfhXMXi40F3ZVvtmIF/S+9COI8VLKUikMUmlmZAw1zWGuhvbltbzobxXindoskh1F2Vcx8VhrmSwuBvcgAfi0hNj+1WMLqkKixNna/K5soPnr9azkWJkjGQMRbcedY7j/FpTKJMzK7HPoTdVv4R111JHpejUPa2J1BlizPYAsGK3sNyBper4tHj+f4OTPPnB0+v2PL1JB0q3DMDodDVOlarjJo9WUUwkQKt43iMs2XvJCwUWVdAq/wooCj4ChkM/JvnVmt00zJpoRpUqJ8H4JJiRIY2jXu8t+8fL7xsoHxosKBlHpOyWKyhkVZborlYnDMFcXUld7HqLjetPFgu4jjHdxYYp+2TExq4lX7TpLrmH7i2IvWZ43x7vAIYcyQxyO0fiYHITdVIvspvbmAbUrb6KqiPDcTECBQmuokTVWV18OcMQdCDYqdiDa1DMXinkJLGwuSEFwq3+6uwqBiSbnUnma5aqshJCpClanxozEKouToAOdIo4KIYDhjSEZyUVgSp08VuQubC/U2FEcNwIKneSPfw5hlt4ba5hfR7bFdKgxvGSLLE2hTKxswW5vfIhPhBB2PwtTr2Ryvo4MTJgpFMbsMy3aNwLgXIKSLcg7X+N9KGYrFNISSdyTbXc6neq5NdqeMeXJLZduqOVJDOyG6kio7VJBCXYKu5Nqbr5Gi0nEPvKD5jT/T6V0TKTuw+R6+lHpuxkiQ5mYLLdh3bEC4Av4Tex01t5VlXTKbGsccseS3B3RpLlHTNBh+NlVEcjd7EBYIwAy6aFGBujb6j612PtAFIOXNZSozcgbaaeYrOUqpwT7IjLi7Ro8V2txDrlVso/d05W9aASyFjcm5qO9dRCxAG5IHz0qlFR6FKbfZwNY3FajDYBJIhJMoYBGfuw4DMNlcgahc2htQ48Mlw7LJlWQK3iC+IC3vK4tpoaIYzEwYc5o0s9svdPnIyOLsrg6bm4y0NWcmTyLpQ3frZzEYSTDqksRDwsqlYZHJIJuHVDuAp5+Yq5wrtlJGMuYqv/rlGZR/C32fgRWcPFXZiz2a56Wt5C2wq1HjYz1BPI6j/AH8KzlC+0dmK0ts3GG7aMAM0YI6xtcWPr+tEI+1eGk1kFiNAWQ3sdwGUkivL3gUm6kAnmhy/harcUTaDO2/M3/EVP0zSz0V+N4Irly3Uk3Wzkai2oPLyrmG4jgowTG8qLuY4lyr8e8JA9RasZhsIp9+RreoHK/Idb0fw0uCjAJKsQL+K77MepI2tR9MLCcvaSWU5MHhjf/2P4yPPXwrQXiKCG8uKkMsnJL6E8r/eH0p/Fu3SKCsC8rX+fTSsFjsdJKxZ2JJrSMKJlIbi8Q0jtIx1Y3P6VBXGam5qsgHWpwtXctdCViWMY1JDMV0O1NYVHRdBVhJWuNKKcB4w+Ek7xRmurIyk2uCNNeoNj8KzschU6fKrsbhhpWsZJkONFmfFSSe/I7ak2ZmOp3Op3qG1KlVEnbV1EubDUnSrWB4ZNLcxxM4G5A0+dbjC9n4ov6Opw0spkCs06tbu3uPsfune/TnScqKSBPB+zBjlQ4uMsliWSM5mQ2uplVdQnprU3argccft1kSM2DLlsFkbQqIUUkhQOZ51a4r2pSCeXu4vbA5TKkhCOQLB2j5kA7Vh8TiGkYs5uSSfmbmwGg16Ukn2Fj8TjnctrlVjmKKTlzczb61VpUqskVKlaiXCuENMd8qjn19BRQN0VMLhHkbKgubXPkBzo0eDxrHmzMCEzFzYAMPsFTqPI1afu8KCM1sykgDU32urDkeh+lZ/HY95SC52FgBf896dJdk230TYjjEskaxu1wnuk6kDpfe3lQ6lSqEklSLbsVq4a7SpgcFF+D8SjjV0kjzBvtDf+E/u+lCaQoM8kFOPF/4NFjOJpF+wykupzkFiFY6HLc63HWs8TXKVAsWKONa2/b7Yq4K7SRCdhf0oNRyknQXJ6UbwHBnkTN3mVrkBSNiPvHlep+zWFjazZvagnwm2g62O9WuK4iNAwcBSy3XJmzFuub1toak87N5cuf04Wn7MzOHRirXDA2IqAsTuaficS0jZnN20F/SowaZ6MbpcuzoNI1y1cNAztNtXa7ekBSrhpUqzLGmmUqVSxnKnwnvUqVOPYS6LtIUqVbmJ612R/wDEg9R+NEsR/wDI9T/lpUqQn2eLy7n1P41HSpU0UztKlSpiJF2NaTgf7OL/AOx/wNKlTj2TPoG9ov2o/hH4mhFKlRPthDpCpUqVSUKlSpUwFSpUqBsVKlSpAKjPZv8AaN6VylSZh5H/ABSJ4/8Azfif8tQ9p/2i/wAP5mlSpI4Y/wDZh/5AppClSpnrHRTWrtKgBUqVKkB//9k=","https://www.istockphoto.com/video/truck-left-hyperlapse-of-river-and-forest-covered-mountains-villarrica-volcano-in-gm1418804393-465371734?utm_source=pixabay&utm_medium=affiliate&utm_campaign=SRP_video_sponsored&utm_content=https%3A%2F%2Fpixabay.com%2Fru%2Fvideos%2Fsearch%2F&utm_term=%D0%BF%D1%80%D0%B8%D1%80%D0%BE%D0%B4%D0%B0","as","as",1,"1000 тг","as","As","as","as","as",new TopCourse.Instructor())));
        mMyCourse.add(new WishListCourse(1, 3, 5, "150", "1.56",new TopCourse(2,1,"Джава",1,"as","as","as","1","data:image/png;base64,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","as","as","as",1,"1000 тг","as","As","as","as","as",new TopCourse.Instructor())));
        mMyCourse.add(new WishListCourse(2, 3, 5, "150", "1.56",new TopCourse(2,1,"Android",1,"as","as","as","1","http://surl.li/hszma","as","as","as",1,"1000 тг","as","As","as","as","as",new TopCourse.Instructor())));
        initMyWishlistGridView(mMyCourse);
        progressBar.setVisibility(View.GONE);
//        ArrayList<WishListCourse> mWishList = new ArrayList<>();
//        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
//        String authToken = preferences.getString("userToken", null);
//        Call<List<WishListCourse>> call = ApiClient.getApi().getMyWishList("Bearer " + authToken);
//        call.enqueue(new Callback<List<WishListCourse>>() {
//            @Override
//            public void onResponse(Call<List<WishListCourse>> call, Response<List<WishListCourse>> response) {
//                List<WishListCourse> myCourseListSchema = response.body();
//
//
//                for (WishListCourse t : myCourseListSchema) {
//                    Log.d("DEBUG_WISHLIST", "onResponse: ");
////                     Log.d("DEBUG_WISHLIST", "onResponse: " + myCourseListSchema.get(0) );
//
//                    mWishList.add(new WishListCourse(t.getId(), t.getUser_id(), t.getCourse_id(), t.getCreated_at(), t.getUpdated_at(), t.getTopCourse()));
//                }
//                if (mWishList.size() > 0) {
//                    initMyWishlistGridView(mWishList);
//                    myWishlistGridLayout.setVisibility(View.VISIBLE);
//                    mEmptyContentArea.setVisibility(View.GONE);
//                } else {
//                    myWishlistGridLayout.setVisibility(View.GONE);
//                    mEmptyContentArea.setVisibility(View.VISIBLE);
//                }
//
//                progressBar.setVisibility(View.INVISIBLE);
//                mSwipeRefreshLayout.setRefreshing(false);
//
//
//            }
//
//            @Override
//            public void onFailure(Call<List<WishListCourse>> call, Throwable t) {
//                mSwipeRefreshLayout.setRefreshing(false);
//                progressBar.setVisibility(View.INVISIBLE);
//            }
//        });
    }

    @Override
    public void removeFromWishList(int courseId) {
        // Auth Token
        SharedPreferences preferences = getContext().getSharedPreferences(Helpers.SHARED_PREF, 0);
        String authToken = preferences.getString("userToken", "");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<StatusSchema> call = api.toggleWishListItems(authToken, courseId);
        call.enqueue(new Callback<StatusSchema>() {
            @Override
            public void onResponse(Call<StatusSchema> call, Response<StatusSchema> response) {
                //getMyWishlist();
            }

            @Override
            public void onFailure(Call<StatusSchema> call, Throwable t) {
                Log.d(TAG, "Wishlist removed Failed");
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(ArrayList<WishListCourse> course) {
        initMyWishlistGridView(course);
    }
}
