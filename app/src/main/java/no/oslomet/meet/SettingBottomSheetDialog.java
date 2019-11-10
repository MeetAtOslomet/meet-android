package no.oslomet.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingBottomSheetDialog extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomaction_settings, container, false);

        TextView txtTerms = v.findViewById(R.id.infoTxtTerms);
        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsIntent= new Intent(getActivity(), ActivityTerms.class);
                getActivity().startActivity(termsIntent);
            }
        });
        //txtTerms.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txtPrivacy = v.findViewById(R.id.infoTxtPrivacy);
        txtPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacyIntent= new Intent(getActivity(),ActivityPrivacy.class);
                getActivity().startActivity(privacyIntent);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
