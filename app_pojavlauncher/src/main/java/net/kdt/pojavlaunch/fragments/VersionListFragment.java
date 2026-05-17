package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VersionListFragment extends Fragment {

    public static final String TAG = "VersionListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = requireContext().getResources().getIdentifier("fragment_version_list", "layout", requireContext().getPackageName());
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int backBtnId = requireContext().getResources().getIdentifier("btn_back_versions", "id", requireContext().getPackageName());
        View backButton = view.findViewById(backBtnId);
        
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
