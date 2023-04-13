package app.playerzero.sdk;


import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerformanceTest {
    private final String[] sampleData = new String[]{
            "4060-4950-3679-3975", "4133-1154-8185-5029", "4979-3824-3531-7376", "4327-6501-2039-5959",
            "4296-1293-8867-9914", "4413-5780-9155-9581", "4796-1538-5839-8255", "4079-7667-6597-5762",
            "5272-0246-1980-7662", "5294-2087-5035-2927", "5275-4319-6722-8473", "5275-4319-6722-8473",
            "3517-7077-2663-1633", "3567-9088-6413-7404", "3582-2853-1179-3308", "3550-6600-6364-6029",
            "3533-8268-0299-5033", "3533-8284-0166-6882", "6478-9243-0408-8322", "6471-5636-1716-3567",
            "4060 4950 3679 3975", "4133 1154 8185 5029", "4979 3824 3531 7376", "4327 6501 2039 5959",
            "4296 1293 8867 9914", "4413 5780 9155 9581", "4796 1538 5839 8255", "4079 7667 6597 5762",
            "5272 0246 1980 7662", "5294 2087 5035 2927", "5275 4319 6722 8473", "5275 4319 6722 8473",
            "3517 7077 2663 1633", "3567 9088 6413 7404", "3582 2853 1179 3308", "3550 6600 6364 6029",
            "3533 8268 0299 5033", "3533 8284 0166 6882", "6478 9243 0408 8322", "6471 5636 1716 3567",
            "4060495036793975", "4133115481855029", "4979382435317376", "4327650120395959",
            "4296129388679914", "4413578091559581", "4796153858398255", "4079766765975762",
            "5272024619807662", "5294208750352927", "5275431967228473", "5275431967228473",
            "3517707726631633", "3567908864137404", "3582285311793308", "3550660063646029",
            "3533826802995033", "3533828401666882", "6478924304088322", "6471563617163567",
            "003-80-1464", "429-55-8022", "481-38-1444", "496-02-0803",
            "659052417", "370147602", "239778943", "386018636",
            "5272-0246-1980-7662", "5294-2087-5035-2927", "5275-4319-6722-8473", "5275-4319-6722-8473",
            "151.77.77.233", "35.67.90.88", "3.5.8.2", "155.166.244.129",
            "John Doe recently made a purchase using his credit card, number 4111-1111-1111-1111, at a local store. He also used his friend's card, number 5555-5555-5555-4444, for some items. He was careful to keep the cards safe and secure, and made sure to redact sensitive information before sharing details about the transactions with anyone. The next day, he received a statement from his bank showing the charges, which he verified using his Mastercard ending in 1234. He was pleased with the prompt and efficient service he received, and planned to use his credit card more in the future.",
            "Lorem ipsum dolor sit amet, 192.168.1.117 adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Quis risus sed vulputate odio ut enim blandit volutpat maecenas. Consectetur a erat nam at lectus urna duis. Venenatis a condimentum vitae sapien pellentesque habitant morbi tristique senectus. Imperdiet nulla malesuada pellentesque elit eget gravida. Mauris ultrices eros in cursus. Faucibus pulvinar elementum integer enim neque volutpat ac tincidunt. Tempor nec feugiat nisl pretium. (770) 404-1972 nec dui nunc mattis enim ut tellus elementum. Proin sagittis nisl rhoncus mattis rhoncus. Pellentesque diam volutpat commodo sed egestas. Et odio pellentesque diam volutpat commodo. Proin fermentum leo vel orci porta non pulvinar neque laoreet. Amet mauris commodo quis imperdiet massa tincidunt.\n" +
                    "\nId venenatis a condimentum vitae sapien pellentesque. Dapibus ultrices in iaculis nunc sed augue lacus viverra vitae. Sed sayan@goku.dbz lacus viverra vitae congue eu consequat ac felis. Consectetur lorem donec massa sapien. Id nibh tortor id aliquet. Libero enim sed faucibus turpis in eu. Id aliquet risus feugiat in ante metus dictum at. Proin fermentum leo vel orci porta non pulvinar. Cras adipiscing enim eu turpis egestas pretium. Sodales neque sodales ut etiam sit amet nisl. Feugiat vivamus at augue eget arcu dictum varius duis. Risus commodo viverra maecenas accumsan lacus vel facilisis. Fames ac turpis egestas integer eget aliquet. Quisque egestas diam in arcu cursus. Nulla pharetra diam sit amet nisl suscipit adipiscing bibendum. Euismod elementum nisi quis eleifend quam adipiscing vitae proin.\n" +
                    "\nNulla pellentesque dignissim enim sit amet venenatis. Vel pretium lectus quam id leo in vitae turpis massa. Mi proin sed libero enim sed faucibus turpis in eu. Vitae nunc sed velit dignissim sodales. Morbi non arcu risus quis varius quam quisque id diam. Amet nulla 257-17-1919 morbi tempus iaculis urna id volutpat. Urna neque viverra justo nec. Vitae ultricies leo integer malesuada nunc vel risus. Id venenatis a condimentum vitae sapien. Tellus elementum sagittis vitae et leo duis. Mauris augue neque gravida in fermentum et sollicitudin ac orci.\n" +
                    "\nNulla aliquet porttitor lacus luctus accumsan tortor posuere. Ipsum a arcu cursus vitae congue. Dui id ornare arcu odio ut sem nulla pharetra. Quis auctor elit sed vulputate. Nunc consequat interdum varius sit amet mattis vulputate. Pellentesque sit amet porttitor eget dolor morbi. Ac ut consequat semper viverra nam. Consectetur lorem Sat Dec 17th, 2019 massa sapien faucibus et molestie ac. Non pulvinar neque laoreet suspendisse interdum consectetur. Non arcu risus quis varius. Ultrices in iaculis nunc sed augue. Mauris vitae ultricies leo integer. Sit amet facilisis magna etiam tempor. Molestie ac feugiat sed lectus. Habitasse platea dictumst vestibulum rhoncus est pellentesque elit ullamcorper.\n" +
                    "\nUt pharetra sit amet aliquam id diam maecenas. Arcu bibendum at varius vel pharetra vel turpis nunc eget. Ultricies integer quis auctor elit sed. Aliquet risus feugiat in ante metus dictum at. Diam ut venenatis tellus in metus vulputate eu. Montes nascetur ridiculus mus mauris vitae. Metus vulputate eu scelerisque felis imperdiet. Sit amet tellus cras adipiscing enim eu turpis. Vel fringilla est ullamcorper eget nulla facilisi etiam dignissim. Pretium lectus quam id leo in vitae turpis massa sed. Arcu vitae elementum curabitur vitae nunc sed velit dignissim. Volutpat consequat mauris nunc congue nisi vitae. Praesent tristique magna sit amet purus gravida quis blandit turpis. Donec et odio pellentesque diam volutpat commodo sed.\n" +
                    "\nPellentesque habitant morbi tristique senectus et netus et. Purus faucibus ornare suspendisse sed. Odio tempor orci dapibus ultrices in iaculis nunc sed. Varius quam quisque id diam vel quam elementum. Vitae auctor eu augue ut lectus arcu bibendum at. Risus ultricies tristique nulla aliquet enim tortor at. Viverra mauris in aliquam sem fringilla ut morbi. Elit sed vulputate mi sit amet mauris commodo quis. Neque ornare aenean euismod elementum nisi quis eleifend quam. Non consectetur a erat nam at lectus urna duis convallis. Et leo duis ut diam quam nulla. Elit scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus. Nulla porttitor massa id neque aliquam vestibulum. Enim tortor at auctor urna nunc. Est ante in nibh mauris cursus mattis molestie a iaculis. Enim eu turpis egestas pretium aenean pharetra. Volutpat commodo sed egestas egestas fringilla phasellus. Rhoncus aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque. Varius quam quisque id diam vel quam elementum pulvinar etiam. Massa massa ultricies mi quis hendrerit dolor magna eget est.\n" +
                    "\nTurpis massa tincidunt dui ut. Metus vulputate eu scelerisque felis imperdiet proin. Tortor id aliquet lectus proin nibh nisl condimentum id venenatis. Egestas egestas fringilla phasellus faucibus scelerisque eleifend donec pretium vulputate. Sapien eget mi proin sed libero enim. Eu ultrices vitae auctor eu augue ut lectus. Lectus arcu bibendum at varius vel pharetra vel turpis. Vel turpis nunc eget lorem. At imperdiet dui accumsan sit. Magna fermentum iaculis eu non. Nullam vehicula ipsum a arcu cursus vitae congue mauris. Accumsan lacus vel facilisis volutpat est velit egestas dui. Euismod elementum nisi quis eleifend quam adipiscing vitae proin sagittis. Viverra nam libero justo laoreet sit. Bibendum ut tristique et egestas quis. Porta lorem mollis aliquam ut porttitor. At lectus urna duis convallis convallis tellus id interdum.\n" +
                    "\nArcu bibendum at varius vel pharetra vel. Commodo sed egestas egestas fringilla phasellus faucibus. Egestas sed sed risus pretium quam vulputate dignissim suspendisse in. Duis convallis convallis tellus id interdum. Convallis convallis tellus id interdum. Nunc vel risus commodo viverra maecenas accumsan lacus. Amet justo donec enim diam vulputate ut pharetra. Justo nec ultrices dui sapien eget mi. Viverra nam libero justo laoreet sit. Quis hendrerit dolor magna eget est lorem. Nisl suscipit adipiscing bibendum est. Ligula ullamcorper malesuada proin libero nunc consequat interdum varius sit. Ac turpis egestas maecenas pharetra convallis posuere. Pellentesque adipiscing commodo elit at imperdiet. Non diam phasellus vestibulum lorem sed risus ultricies tristique. Fermentum posuere urna nec tincidunt praesent.\n" +
                    "\nUltricies mi eget mauris pharetra et ultrices neque ornare aenean. Nullam vehicula ipsum a arcu cursus vitae. Eu turpis egestas pretium aenean pharetra magna ac placerat. Platea dictumst vestibulum rhoncus est pellentesque elit ullamcorper dignissim. Nisl purus in mollis nunc sed id semper. Est pellentesque elit ullamcorper dignissim cras tincidunt. Risus viverra adipiscing at in tellus integer. Bibendum neque egestas congue quisque egestas diam in arcu. Ipsum dolor sit amet consectetur. Sed viverra tellus in hac. Ornare arcu odio ut sem nulla. Amet consectetur adipiscing elit ut aliquam. Id porta nibh venenatis cras sed felis eget. Cursus euismod quis viverra nibh cras pulvinar mattis. Blandit turpis cursus in hac habitasse platea. Nisi est sit amet facilisis. Faucibus et molestie ac feugiat sed lectus vestibulum mattis ullamcorper.\n" +
                    "\nTortor pretium viverra suspendisse potenti nullam ac tortor vitae. Enim sit amet venenatis urna cursus eget nunc scelerisque viverra. Tortor id aliquet lectus proin nibh. Sagittis id consectetur purus ut faucibus pulvinar. Quis lectus nulla at volutpat. Eros donec ac odio tempor. Platea dictumst quisque sagittis purus sit. Molestie nunc non blandit massa enim. Bibendum est ultricies integer quis. Eu nisl nunc mi ipsum faucibus. Cursus eget nunc scelerisque viverra mauris. Nunc sed augue lacus viverra vitae congue eu consequat. Eget velit aliquet sagittis id consectetur purus ut faucibus pulvinar. Orci ac auctor augue mauris augue neque. Eu tincidunt tortor aliquam nulla. Adipiscing commodo elit at imperdiet. Cursus turpis massa tincidunt dui. Tempus quam pellentesque nec nam.\n" +
                    "\nSemper viverra nam libero justo. Varius duis at consectetur lorem. Augue neque gravida in fermentum et sollicitudin ac. Amet nisl suscipit adipiscing bibendum est ultricies. Tempus iaculis urna id volutpat. Egestas sed tempus urna et. Est pellentesque elit ullamcorper dignissim cras tincidunt lobortis feugiat. Suscipit adipiscing bibendum est ultricies. Eu sem integer vitae justo eget magna fermentum iaculis eu. Aenean vel elit scelerisque mauris pellentesque pulvinar pellentesque. Tellus rutrum tellus pellentesque eu tincidunt tortor aliquam. Enim ut sem viverra aliquet eget sit amet tellus. Et ultrices neque ornare aenean euismod.\n" +
                    "\nVolutpat diam ut venenatis tellus. Nulla porttitor massa id neque aliquam vestibulum morbi. Euismod elementum nisi quis eleifend quam adipiscing vitae proin sagittis. Arcu dui vivamus arcu felis bibendum ut tristique et egestas. Turpis egestas pretium aenean pharetra magna ac placerat. Sed turpis tincidunt id aliquet risus feugiat in. In ornare quam viverra orci sagittis eu volutpat odio facilisis. Sapien eget mi proin sed libero. Amet volutpat consequat mauris nunc congue nisi. Ut sem viverra aliquet eget sit. Feugiat in fermentum posuere urna nec.\n" +
                    "\nConsectetur libero id faucibus nisl tincidunt eget nullam non. Tincidunt praesent semper feugiat nibh sed. Amet massa vitae tortor condimentum lacinia quis. Odio euismod lacinia at quis risus sed vulputate odio ut. Pellentesque elit ullamcorper dignissim cras tincidunt lobortis. Tellus in metus vulputate eu scelerisque felis. Sit amet tellus cras adipiscing enim eu turpis. Commodo viverra maecenas accumsan lacus vel facilisis volutpat est. Non enim praesent elementum facilisis leo. Amet risus nullam eget felis eget nunc lobortis. Eu sem integer vitae justo. Mauris augue neque gravida in fermentum et sollicitudin ac. Bibendum ut tristique et egestas. Scelerisque mauris pellentesque pulvinar pellentesque habitant morbi tristique senectus et. Ullamcorper velit sed ullamcorper morbi. Tempor nec feugiat nisl pretium fusce id velit. Id consectetur purus ut faucibus pulvinar. Urna cursus eget nunc scelerisque viverra. Praesent tristique magna sit amet purus gravida quis blandit turpis.\n" +
                    "\nTincidunt id aliquet risus feugiat. Maecenas sed enim ut sem viverra. Aliquam ut porttitor leo a diam sollicitudin tempor id eu. Enim nulla aliquet porttitor lacus luctus. Ridiculus mus mauris vitae ultricies. A diam maecenas sed enim ut sem viverra. Blandit aliquam etiam erat velit scelerisque in dictum non consectetur. Eget egestas purus viverra accumsan in nisl. At quis risus sed vulputate odio ut. Enim nunc faucibus a pellentesque sit amet. Habitasse platea dictumst vestibulum rhoncus est pellentesque. Et molestie ac feugiat sed lectus vestibulum. Mi tempus imperdiet nulla malesuada pellentesque elit eget gravida cum. Feugiat in fermentum posuere urna nec. Sed elementum tempus egestas sed. In arcu cursus euismod quis viverra nibh. Molestie a iaculis at erat pellentesque adipiscing. Id velit ut tortor pretium viverra. Libero justo laoreet sit amet cursus sit amet dictum.\n" +
                    "\nBlandit volutpat maecenas volutpat blandit aliquam etiam. Sit amet nulla facilisi morbi. Pulvinar neque laoreet suspendisse interdum consectetur libero id faucibus. Malesuada proin libero nunc consequat interdum varius sit amet. Aliquam sem fringilla ut morbi. Rhoncus aenean vel elit scelerisque mauris. Porttitor leo a diam sollicitudin tempor id eu nisl. Dui sapien eget mi proin sed libero enim sed. Nullam vehicula ipsum a arcu cursus vitae congue mauris rhoncus. Integer eget aliquet nibh praesent tristique magna sit amet. Suspendisse potenti nullam ac tortor vitae purus. Ante in nibh mauris cursus mattis molestie. Dignissim enim sit amet venenatis urna cursus eget nunc scelerisque. Vehicula ipsum a arcu cursus vitae.",
            "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                    "b3BlbnNzaC1rZXktdjEAAAAACmFlczI1Ni1jdHIAAAAGYmNyeXB0AAAAGAAAABBJekby3L\n" +
                    "g5qefTtF4jAAbbRetBv3+sM46R4AjSrmPKIgD593DiH1xq+LH7TN2/1CJ/1VXzCfvU6pI+\n" +
                    "0PEbwMXjceriiFNDP09WvfAJBF9nFSR+jAYG3NpbOBvUfRMOtXfJQtEsFjOVyt8fCvnhRY\n" +
                    "xswKNm6QmjfUFQFks1BybtWjvHTDPUeZ0PiwZ7LldvJdHCDUpvnRAgCTMROI9w2tgOZ3O8\n" +
                    "AkZq2iHVcoDuEtdUty98cX7ji7NiuvdP4sZio2ykiVqWJm3JviqIb2GshmRaL/ZpWAvgMG\n" +
                    "hzg4EdH9rklCsAAAWQu5IQGBUHfwOLtVhLFvxyH3hXP8TZGFgKSaL3YxvgWOI7Q9HNoibB\n" +
                    "dhEkhNnKeqCmrTxOLSY8nmfzyFs3PzsLQbAFnBie0ckkSxHOR7DpkMHcXqm4IpeJIoI2c2\n" +
                    "wqut1rESoznRWp2vaW1+aaJgod+Q1iHpGdAz0oFLCJt94pmeegBFH+sztrwy8iEq27ROX6\n" +
                    "-----END OPENSSH PRIVATE KEY-----\n"
    };
    private final Pattern[] patterns = new Pattern[]{
//            Pattern.compile("\\b\\d{3}-?\\d{2}-?\\d{4}\\b", Pattern.MULTILINE),
//            Pattern.compile("\\b(?:(?:\\d{3,4})[ -]?){4}\\b", Pattern.MULTILINE),
//            Pattern.compile("\\b(\\d{10})|(([(]?([0-9]{3})[)]?)?[ .\\-]?([0-9]{3})[ .\\-]([0-9]{4}))\\b", Pattern.MULTILINE),
//            Pattern.compile("\\b([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)\\b", Pattern.MULTILINE),
//            Pattern.compile("\\b[0-9a-fA-F]{16,}\\b", Pattern.MULTILINE),
//            Pattern.compile("^[0-9a-zA-Z+/]{1,70}={0,3}$", Pattern.MULTILINE),
//            Pattern.compile("\\b[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{12}\\b", Pattern.MULTILINE),
//            Pattern.compile("\\bhttps?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\\b", Pattern.MULTILINE),
            Pattern.compile("\\b\\d{3}-?\\d{2}-?\\d{4}\\b" +
                    "|\\b(?:(?:\\d{3,4})[ -]?){4}\\b" +
                    "|\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b" +
                    "|\\b([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)\\b" +
                    "|\\b[0-9a-fA-F]{16,}\\b" +
                    "|^[0-9a-zA-Z+/]{1,70}={0,3}$" +
                    "|\\b[0-9a-fA-F]{8}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{4}-?[0-9a-fA-F]{12}\\b" +
                    "|\\bhttps?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\\b", Pattern.MULTILINE),
    };

    @Test
    public void testRegexAccuracy() {
//        System.out.printf("\n\n%s\n", sampleData[0].replaceAll("\\b(?:(?:\\d{3,4})[ -]?){4}\\b", "<redact>"));
        for (Pattern pattern : patterns) {
            System.out.printf("Pattern: %s\n", pattern.toString());
            for (String data : sampleData) {
                Matcher matcher = pattern.matcher(data);
                System.out.println(matcher.replaceAll("<redact>"));

            }
        }
    }
}