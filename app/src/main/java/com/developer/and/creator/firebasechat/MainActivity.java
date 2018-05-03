package com.developer.and.creator.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //создаем константу
    private static int SIGN_IN_REQUEST_CODE = 1;

    //дженерик который поддерживает поддержку сообщений
    private FirebaseListAdapter<Message> adapter;

    //обьявляем корневой макет экрана и кнопку отправки сообщения
    RelativeLayout activity_main;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //находим корневой лайаут по id
        activity_main = (RelativeLayout)findViewById(R.id.activity_main);

        //находим кнопку по id
        button = (Button)findViewById(R.id.button_send);

        //добавляем обработчик нажатия по id кнопки
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //опеределяем поле ввода
                EditText input = (EditText)findViewById(R.id.editText);

                //считываем введеный текст с поля ввода и отправляем в базу данных firebase
                FirebaseDatabase.getInstance().getReference().push()
                        .setValue(new Message(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                //обнуляем поле ввода после клика
                input.setText("");
            }
        });

        //если пользователь не авторизован, то показываем ему окно авторизации сначала
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            //если же пользователь авторизован то показываем ему чат
            displayChat();
        }
    }

    //создаем метод для показа авторизации
    private void displayChat() {
        //создаем список сообщений
        ListView listMessages = (ListView)findViewById(R.id.listView);

        //создаем адаптер списка
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.item,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                //пункты/разделы списка
                TextView textMessage, author, timeMessage;

                //определяем поля пунктов списка по id
                textMessage = (TextView)v.findViewById(R.id.tvMessage);
                author = (TextView)v.findViewById(R.id.tvUser);
                timeMessage = (TextView)v.findViewById(R.id.tvTime);

                //сообщение, имя пользователя
                textMessage.setText(model.getTextMessage());
                author.setText(model.getAuthor());
                timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimeMessage()));
            }
        };

        //передаем adapter списку
        listMessages.setAdapter(adapter);
    }

    //результат в котором мы вызываем окно авторизации
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            //удачный вход
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Вход выполнен", Snackbar.LENGTH_SHORT).show();
                displayChat();
            } else {
                //если вход не выполнен, показываем о неудаче пользователю
                Snackbar.make(activity_main, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //вход/выход через меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //проверка выбраного пункта меню и выход из учетной записи чата
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signout) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //в случае успеха отображаем уведомление
                    Snackbar.make(activity_main, "Выход выполнен", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }
}
