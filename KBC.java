import java.util.*;

class Question {
    String question;
    String[] options;
    int correctAnswer;
    String category;
    String difficulty;

    Question(String question, String[] options, int correctAnswer, String category, String difficulty) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }
}

class KBC {
    Player player;
    List<Question> questions;
    int currentQuestionIndex;
    int[] prizeHives = {500, 1000, 2000, 5000, 10000, 20000, 40000, 80000, 160000, 320000, 640000, 1000000, 7000000};
    boolean[] lifeLines = {true, true, true};
    static final int MAX_TIME = 30; 
    static List<PlayerScore> leaderboard = new ArrayList<>();

   KBC(String playerName, String difficultyLevel, String category) {
        this.player = new Player(playerName);
        this.questions = loadQuestions(difficultyLevel, category);
        this.currentQuestionIndex = 0;
    }

    List<Question> loadQuestions(String difficultyLevel, String category) {
        List<Question> allQuestions = new ArrayList<>();

       
        allQuestions.add(new Question("how many continents are there?", new String[]{"5", "7", "3", "9"}, 1, "General Knowledge", "Easy"));
        allQuestions.add(new Question("What is the smallest unit of matter?", new String[]{"Atom", "Molecule", "Cell", "Electron"}, 0, "Science", "Easy"));
        allQuestions.add(new Question("What is the currency of Japan?", new String[]{"Yuan", "Rupee", "Yen", "Won"}, 2, "General Knowledge", "Medium"));
        allQuestions.add(new Question("Who is the CEO of GOOGLE?", new String[]{"Bill Gates", "Mark Zuckerberg", "Sundar Pichayi", "Larry Page"}, 2, "Business", "Medium"));
        allQuestions.add(new Question("In which country is the Great Barrier Reef located?", new String[]{"Australia", "New Zealand", "USA", "South Africa"}, 0, "General Knowledge", "Hard"));
        allQuestions.add(new Question("Who invented the telephone?", new String[]{"Alexander Graham Bell", "Thomas Edison", "Nikola Tesla", "Albert Einstein"}, 0, "Science", "Medium"));
        allQuestions.add(new Question("What is the largest ocean on Earth?", new String[]{"Atlantic", "Indian", "Pacific", "Arctic"}, 2, "Geography", "Easy"));
        allQuestions.add(new Question("Which is the longest river in the world?", new String[]{"Amazon", "Nile", "Yangtze", "Ganges"}, 1, "Geography", "Medium"));
        allQuestions.add(new Question("Who is the founder of BMW?", new String[]{"Steve Jobs", "Karl Rapp", "Mark Zuckerberg", "Elon Musk"}, 1, "Business", "Hard"));
        allQuestions.add(new Question("What is the chemical symbol for Platinum?", new String[]{"Pt", "Ag", "Fe", "Pb"}, 0, "Science", "Hard"));
        allQuestions.add(new Question("Who is the father of 'Shivaji Maharaj'?", new String[]{"Tanhaji", "Shambhaji", "Shahaji", "Maloji"}, 2, "History", "Medium"));
        allQuestions.add(new Question("Who wrote the 'why I am Atheist?", new String[]{"Bhagat Singh", "Mahatma Gandhi", "Chetan Bhagat", "Subhash Chandra Bose"}, 0, "History", "Hard"));
        allQuestions.add(new Question("What do you call group of lions?", new String[]{"A pride", "A herd", "A flock", "A school"}, 0, "Nature", "Easy"));

 
        List<Question> filteredQuestions = new ArrayList<>();
        for (Question q : allQuestions) {
            if ((q.category.equalsIgnoreCase(category) || category.equalsIgnoreCase("All")) &&
                (q.difficulty.equalsIgnoreCase(difficultyLevel) || difficultyLevel.equalsIgnoreCase("All"))) {
                filteredQuestions.add(q);
            }
        }
        return filteredQuestions;
    }

    void play() {
        Scanner sc = new Scanner(System.in);

        if (questions.isEmpty()) {
            System.out.println("No questions available for the selected category and difficulty level.");
            return;
        }

        int totalPrizeMoney = 0; 

        while (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            System.out.println("\nCategory: " + currentQuestion.category + " | Difficulty: " + currentQuestion.difficulty);
            System.out.println("Question " + (currentQuestionIndex + 1) + ": " + currentQuestion.question);
            for (int i = 0; i < currentQuestion.options.length; i++) {
                System.out.println((i + 1) + ". " + currentQuestion.options[i]);
            }

            
            System.out.println("\nUse Lifeline (1 for 50-50, 2 for Audience Poll, 3 for Expert Advice, 0 to skip, or press any other key to continue without lifeline): ");
            int lifelineChoice = sc.nextInt();

            if (lifelineChoice > 0 && lifelineChoice <= 3 && lifeLines[lifelineChoice - 1]) {
                useLifeline(currentQuestion, lifelineChoice);
                lifeLines[lifelineChoice - 1] = false;
            } else if (lifelineChoice == 0) {
                
                System.out.println("Skipping question. No prize money awarded for skipped questions.");
                currentQuestionIndex++;
                continue;
            } else if (lifelineChoice > 0 && lifelineChoice <= 3 && !lifeLines[lifelineChoice - 1]) {
                System.out.println("Lifeline already used.");
            }

           
            TimerThread timerThread = new TimerThread();
            Thread timer = new Thread(timerThread);
            timer.start();

           
            System.out.print("\nYour answer (1-" + currentQuestion.options.length + "): ");
            int answer = sc.nextInt();

           
            timerThread.stopTimer();

            if (answer == currentQuestion.correctAnswer + 1) {
                System.out.println("Correct!");
                int prizeMoneyForThisQuestion = prizeHives[currentQuestionIndex]; 
                totalPrizeMoney += prizeMoneyForThisQuestion; 
                player.win(prizeMoneyForThisQuestion); 
                System.out.println("You have won: Rs. " + prizeMoneyForThisQuestion);
                System.out.println("Total Prize Money so far: Rs. " + totalPrizeMoney); 
                currentQuestionIndex++;
            } else {
                System.out.println("Incorrect!");
                System.out.println("Game Over! You won Rs. " + totalPrizeMoney);
                break; 
            }

          
            if (currentQuestionIndex == questions.size()) {
                System.out.println("\nCongratulations! You've answered all the questions correctly!");
                System.out.println("Total Prize Money: Rs. " + totalPrizeMoney);
                break;
            }
        }

        
        leaderboard.add(new PlayerScore(player.getName(), totalPrizeMoney));
        
        
        leaderboard.sort((p1, p2) -> Integer.compare(p2.getPrizeMoney(), p1.getPrizeMoney()));
        
      
        System.out.println("\nLeaderboard:");
        for (int i = 0; i < leaderboard.size(); i++) {
            PlayerScore score = leaderboard.get(i);
            System.out.println((i + 1) + ". " + score.getName() + " - Rs. " + score.getPrizeMoney());
        }

        
        System.out.println("\nGame Over! Final Prize: Rs. " + totalPrizeMoney);
       
    }

     void useLifeline(Question question, int lifelineType) {
        Random random = new Random();
        switch (lifelineType) {
            case 1: 
                int incorrectOption;
                do {
                    incorrectOption = random.nextInt(question.options.length);
                } while (incorrectOption == question.correctAnswer);

                System.out.println("50-50: Two options remain:");
                int remainingOption1 = question.correctAnswer;
                int remainingOption2;
                do {
                    remainingOption2 = random.nextInt(question.options.length);
                } while (remainingOption2 == question.correctAnswer || remainingOption2 == incorrectOption);
                System.out.println((remainingOption1 + 1) + ". " + question.options[remainingOption1]);
                System.out.println((remainingOption2 + 1) + ". " + question.options[remainingOption2]);
                break;

            case 2: 
                System.out.println("Audience Poll: (Simulated)");
                for (int i = 0; i < question.options.length; i++) {
                    int percentage = random.nextInt(100);
                    System.out.println((i + 1) + ". " + percentage + "%");
                }
                break;

            case 3:
                System.out.println("Expert Advice: (Simulated)");
                int expertAnswer = random.nextInt(question.options.length);
                System.out.println("Expert suggests: " + (expertAnswer + 1) + ". " + question.options[expertAnswer]);
                break;
        }
    }

     class Player {
         String name;
         int prizeMoney;

      Player(String name) {
            this.name = name;
            this.prizeMoney = 0;
        }

      void win(int amount) {
            this.prizeMoney = amount;
        }

      int getPrizeMoney() {
            return this.prizeMoney;
        }

      String getName() {
            return name;
        }
    }

    class PlayerScore {
         String name;
         int prizeMoney;

         PlayerScore(String name, int prizeMoney) {
            this.name = name;
            this.prizeMoney = prizeMoney;
        }

         String getName() {
            return name;
        }

         int getPrizeMoney() {
            return prizeMoney;
        }
    }

     class TimerThread implements Runnable {
         boolean isRunning = true;
         int timeLeft = MAX_TIME;

       
        public void run() {
            try {
                while (timeLeft > 0 && isRunning) {
                    System.out.print("\rTime remaining: " + timeLeft + " seconds");
                    Thread.sleep(1000);
                    timeLeft--;
                }
                if (timeLeft <= 0) {
                    System.out.println("\nTime's up!");
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stopTimer() {
            isRunning = false; 
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String playerName = sc.nextLine();

        
        System.out.print("Choose category (All/General Knowledge/Science/History/Geography/Business): ");
        String category = sc.nextLine();

        System.out.print("Choose difficulty (All/Easy/Medium/Hard): ");
        String difficulty = sc.nextLine();

      
        KBC game = new KBC(playerName, difficulty, category);
        game.play();
    }
}