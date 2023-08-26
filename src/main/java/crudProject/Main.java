package crudProject;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static void main(String[] args) {

        Session session = SessionJDBC.sessionFactory.openSession();

        System.out.println("Добро пожаловать!");
        System.out.println("Пример команд: \n-c имя фамилия(создание).\n" +
                "-r(увидеть все задачи). \n-u id_клиента(изменение). \n-d id_клиента(удаление).");

        boolean flag = true;

        while (flag){

            System.out.println("Введите команду(-c, -r, -u, -d). Для выхода \"q\".");

            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            String[] commandArr = command.split(" ", 3);
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            Date date;
            Transaction transaction = session.beginTransaction();

            switch (commandArr[0]){

                case "-c":
                    if (commandArr.length < 3){
                        System.out.println("Некорректные данные, попробуйте ещё раз.");
                        break;
                    }

                    Client client = new Client();
                    client.setName(commandArr[1]);
                    client.setLastName(commandArr[2]);

                    System.out.print("Введите номер телефона: ");
                    String number = scanner.nextLine();
                    if (!number.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")){
                        System.out.println("Некорректный номер телефона. Попробуйте ещё раз.");
                        break;
                    }
                    client.setNumber(number);

                    System.out.print("Введите e-mail(Необязательное поле, оставьте пустым): ");
                    String email = scanner.nextLine();
                    if (!email.equals("")){
                        if (email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+")){
                            client.setEmail(email);
                        }else {
                            System.out.println("Некорректный email. Попробуйте ещё раз.");
                            break;
                        }
                    }

                    System.out.print("Введите дату. Формат даты: dd/MM/yyyy: ");
                    String dateSet = scanner.nextLine();
                    try {
                        date = simpleDateFormat.parse(dateSet);
                    } catch (ParseException e) {
                        System.out.println("Некорректная дата. Попробуйте ещё раз.");
                        break;
                    }
                    client.setBirthDate(date);

                    session.persist(client);
                    break;

                case "-r":
                    String hql = "From " + Client.class.getSimpleName();
                    List<Client> clients = session.createQuery(hql).getResultList();

                    for (Client c : clients) {
                        System.out.println(c.getId() + ". " +c.getName() + " " + c.getLastName() + " " + c.getNumber()
                                          + " " + c.getEmail() + " " + formatter.format(c.getBirthDate()));
                    }
                    break;

                case "-u":
                    if (commandArr.length < 2){
                        System.out.println("Некорректные данные, попробуйте ещё раз.");
                        break;
                    }
                    if (commandArr[1].matches("\\D")){
                        System.out.println("Некорректные данные, попробуйте ещё раз.");
                        break;
                    }

                    client = session.get(Client.class, commandArr[1]);

                    System.out.println("Введите изменения: ");
                    System.out.print("Введите имя: ");
                    String name = scanner.nextLine();
                    client.setName(name);

                    System.out.print("Введите фамилию: ");
                    String lastName = scanner.nextLine();
                    client.setLastName(lastName);

                    System.out.print("Введите номер телефона: ");
                    number = scanner.nextLine();
                    if (!number.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")){
                        System.out.println("Некорректный номер телефона. Попробуйте ещё раз.");
                        break;
                    }
                    client.setNumber(number);

                    System.out.print("Введите e-mail(Необязательное поле, оставьте пустым): ");
                    String email1 = scanner.nextLine();
                    if (!email1.equals("")){
                        if (email1.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+")){
                            client.setEmail(email1);
                        }else {
                            System.out.println("Некорректный email. Попробуйте ещё раз.");
                            break;
                        }
                    }

                    System.out.print("Введите дату. Формат даты: dd/MM/yyyy: ");
                    dateSet = scanner.nextLine();
                    try {
                        date = simpleDateFormat.parse(dateSet);
                    } catch (ParseException e) {
                        System.out.println("Некорректная дата. Попробуйте ещё раз.");
                        break;
                    }
                    client.setBirthDate(date);

                    session.merge(client);
                    break;

                case "-d":
                    if (commandArr.length < 2){
                        System.out.println("Некорректные данные, попробуйте ещё раз.");
                        break;
                    }
                    if (commandArr[1].matches("\\D")){
                        System.out.println("Некорректные данные, попробуйте ещё раз.");
                        break;
                    }
                    Client client2 = session.get(Client.class, commandArr[1]);
                    session.remove(client2);
                    break;

                case "q":
                    System.out.println("До свидания!");
                    session.clear();
                    flag = false;
                    break;

                default:
                    System.out.println("Неверная команда!" + commandArr[0] + "\nПопробуйте снова! ");

            }
            transaction.commit();
        }
    }
}