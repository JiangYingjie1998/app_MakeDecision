package edu.cczu.makedecision.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import edu.cczu.makedecision.R;
import edu.cczu.makedecision.data.Choice;
import edu.cczu.makedecision.data.ChoiceDataSource;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DecideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DecideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ChoiceDataSource dataSource;
    private List<Choice> choices;
    private ListView listView;
    private static final int CONTEXT_MENU_view = 0;
    private static final int CONTEXT_MENU_edit = 1;
    private static final int CONTEXT_MENU_delete = 2;

    private SharedPreferences titlePref;

    private Context mContext;

    private void displayAllChoices() {
        choices = dataSource.findAll();
        ArrayAdapter<Choice> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_expandable_list_item_1, choices);
        if (listView != null) listView.setAdapter(adapter);
    }

    private OnFragmentInteractionListener mListener;

    public DecideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DecideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DecideFragment newInstance(String param1, String param2) {
        DecideFragment fragment = new DecideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        dataSource = new ChoiceDataSource(mContext);
        titlePref = dataSource.getTitlePrefs();
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }


//        displayAllChoices();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_decide, container, false);
        final TextView textView_Event = view.findViewById(R.id.textView_Event);
        if (titlePref != null)
            textView_Event.setText(titlePref.getString("title", "吃什么"));
        listView = view.findViewById(R.id.ListView_choices);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, CONTEXT_MENU_view, 0, "查看");
                contextMenu.add(0, CONTEXT_MENU_edit, 1, "编辑");
                contextMenu.add(0, CONTEXT_MENU_delete, 2, "删除");

            }
        });
        //Decide Now
        Button button_decide = view.findViewById(R.id.button_decide);
        button_decide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choices.size() > 0) {
                    int randPosition = new Random().nextInt(choices.size());
                    Choice selectedChoice = choices.get(randPosition);
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    TextView textView = new TextView(mContext);
                    textView.setTextSize(24);
                    textView.setText("Decision has been made!\n\t\tGo with " + selectedChoice.getName());
                    alert.setView(textView);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    TextView textView = new TextView(mContext);
                    textView.setTextSize(24);
                    textView.setText("Please add a choice before you make a decision!");
                    alert.setView(textView);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }
        });

        //add a choice
        Button button_add = view.findViewById(R.id.button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createChoice();
                Choice choice = new Choice();
                updateChoiceFromInput(choice);
            }
        });

        //modify the title
        Button button_modifyTitle = view.findViewById(R.id.button_modifyTitle);
        button_modifyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                final EditText input = new EditText(mContext);
                input.setText(textView_Event.getText());
                alert.setView(input);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String updatedTitle = input.getText().toString();
                        textView_Event.setText(updatedTitle);
                        SharedPreferences.Editor editor = titlePref.edit();
                        editor.putString("title", updatedTitle);
                        editor.commit();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                alert.show();


            }
        });

        //clear all
        Button button_clearAll = view.findViewById(R.id.button_clearAll);
        button_clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                TextView title = new TextView(mContext);
                title.setText("Are you sure to clear all?");
                title.setTextSize(24);
                alert.setView(title);

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        for (Choice choice : choices)
                            dataSource.remove(choice);
                        displayAllChoices();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close the dialog box and do nothing
                        dialog.cancel();
                    }
                });

                alert.show();
            }
        });

        displayAllChoices();
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;
        Choice choice = choices.get(id);
        switch (item.getItemId()) {
            default:
                return false;
            case CONTEXT_MENU_view:
                Toast.makeText(getActivity().getApplicationContext(), choice.getName() + "exist", Toast.LENGTH_SHORT);
                return true;
            case CONTEXT_MENU_edit:
                updateChoiceFromInput(choice);
                return true;
            case CONTEXT_MENU_delete:
                dataSource.remove(choice);
                displayAllChoices();
                return true;
        }
    }

    private void updateChoiceFromInput(final Choice choice) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        final EditText input = new EditText(mContext);
        input.setText(choice.getName());
        alert.setView(input);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String updatedName = input.getText().toString();
                choice.setName(updatedName);
                dataSource.save(choice);
                displayAllChoices();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
            }
        });

        alert.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void MakeDecision() {
        if (choices.size() > 0) {
            int randPosition = new Random().nextInt(choices.size());
            Choice selectedChoice = choices.get(randPosition);
            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            TextView textView = new TextView(mContext);
            textView.setTextSize(24);
            textView.setText("Decision has been made!\n\t\tGo with " + selectedChoice.getName());
            alert.setView(textView);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
