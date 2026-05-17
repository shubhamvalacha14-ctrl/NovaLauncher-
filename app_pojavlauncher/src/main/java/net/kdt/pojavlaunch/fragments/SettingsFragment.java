package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.kdt.pojavlaunch.R;

public class SettingsFragment extends Fragment {

    public static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the premium layout view we created earlier
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle the Zalith close cross button transaction
        View closeButton = view.findViewById(R.id.btn_close_settings);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                // Smoothly slide out backwards and pop back to the main menu row
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
