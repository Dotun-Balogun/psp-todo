# PSP Tasks — To-Do List Task Management System
Plateau State Polytechnic, Barkin Ladi · Built with Java (Spring Boot + Thymeleaf) · Database: Supabase (PostgreSQL) · Hosting: Render

Everything (pages and backend) is written in Java. No JavaScript framework, no Tailwind. Works on phones and desktops.

**What you get:** register and sign in (with show/hide password), add / edit / delete / complete tasks, priority, category, due date, search, filter, sort, views (All, Today, Upcoming, Overdue, Completed), a dashboard with counts and a progress bar, clear error messages, and a crest-coloured design (crimson, gold, charcoal).

---
## 1. Install the tools (one time)
1. **Java JDK 17**: download "Temurin 17" from https://adoptium.net and install it.
2. **Maven** (you already have this).
3. **Git**: https://git-scm.com (needed to deploy).
4. Check in a terminal (Command Prompt / PowerShell):
```
java -version     (should say 17)
mvn -v            (should print a version, and Java 17)
```
If `mvn -v` shows a different Java version, set the `JAVA_HOME` environment variable to your JDK 17 folder.

## 2. Create the database (Supabase, free)
1. Sign up at https://supabase.com → **New project**. Pick a name, set a **database password** (save it!) and a region near you. Wait about 2 minutes.
2. Click **Connect** at the top of the dashboard → **Session pooler**. Copy the **host**, **port (5432)** and **user** (it looks like `postgres.abcdxyz`).
   Use the *Session pooler*, not "Direct connection": Render cannot use the direct one.
3. Build your three values:
```
DB_URL      = jdbc:postgresql://<HOST>:5432/postgres?sslmode=require
DB_USER     = postgres.<your-project-ref>
DB_PASSWORD = <the password you chose>
```
You do **not** create tables. The app creates them itself the first time it starts.

## 3. Run it on your computer
1. Unzip this project and open a terminal inside the folder (the one containing `pom.xml`).
2. Create the file `src/main/resources/application-local.properties` with your values (this file is git-ignored, so it never gets uploaded):
```
DB_URL=jdbc:postgresql://<HOST>:5432/postgres?sslmode=require
DB_USER=postgres.<your-project-ref>
DB_PASSWORD=<your password>
ADMIN_PASSWORD=<pick a password for the admin account>
```
3. Start the app:
```
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
(PowerShell: put the last part in quotes: `"-Dspring-boot.run.profiles=local"`)
4. Open **http://localhost:8080**, click **Create an account**, and try it. Stop the app with `Ctrl + C`.

The first run downloads libraries, so it takes a few minutes.

## 4. Put it online (Render)
1. Create a free account on https://github.com and a **new empty repository**.
2. In the project folder run:
```
git init
git add .
git commit -m "PSP Tasks"
git branch -M main
git remote add origin https://github.com/<you>/<repo>.git
git push -u origin main
```
3. On https://render.com: **New → Web Service** → connect your GitHub repo.
4. Settings: **Language/Runtime = Docker** (Render finds the `Dockerfile` by itself). Pick the free instance.
5. Under **Environment Variables** add: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `ADMIN_PASSWORD` (same values as above).
6. Under **Advanced** set **Health Check Path** to `/login`.
7. Click **Create Web Service**. The first build takes 5–10 minutes. When it says *Live*, open the link Render gives you.

Free Render services sleep after ~15 minutes without visitors, so the first page load afterwards can take up to a minute. That is normal.

## 5. Using the app
- **Admin account:** username `admin` and the `ADMIN_PASSWORD` you set. Change it by updating the variable (only used when the account is first created).
- Sign in with your **username or email**.
- Default categories: School, Personal, Project, Exam.

## 6. Project map (where things are)
```
pom.xml                         Maven build file (libraries)
Dockerfile                      How Render builds and runs the app
src/main/java/com/psp/todo/
  model/       User, Task, Category, Priority     (the data: "M" in MVC)
  repo/        database access                    (the DAO layer)
  web/         controllers + security + errors    ("C" in MVC)
src/main/resources/
  templates/   the pages (HTML with Thymeleaf)    ("V" in MVC)
  static/      css, js, crest image
  application.properties   settings
```

## 7. If something goes wrong
| You see | What to do |
|---|---|
| App won't start, "Failed to determine a suitable driver" or `${DB_URL}` error | The database values are missing. Recheck step 3.2 (local) or the Render environment variables. |
| "Connection refused" / "timeout" / "Network is unreachable" | You used the Direct connection. Use the **Session pooler** host. |
| "password authentication failed" | Wrong `DB_PASSWORD`, or `DB_USER` is missing the `postgres.` prefix and project ref. |
| Login says "We can't reach the database" | Supabase project is paused (free projects pause after inactivity). Open the Supabase dashboard and restore it. |
| Login says "Incorrect username, email or password" | Really a wrong detail. Use the eye icon to check what you typed. |
| Page keeps sending you back to login on Render | Make sure you did not remove `server.forward-headers-strategy=framework` in `application.properties`. |
| `mvn` not found / wrong Java | Redo step 1 and check `JAVA_HOME`. |
| Render build fails | Open the **Logs** tab in Render and read the last red lines. |

## 8. Not built yet (planned next)
Forgot-password by email, due-date reminders, subtasks, recurring tasks, charts, admin screens for managing categories, and installable-app (PWA) mode.
