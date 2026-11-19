package com.spring.springbootapplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/skill-edit")
    public String showSkillEditPage(
        @RequestParam(value = "month", required = false) String monthStr,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        // 当月の1日を取得
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        // プルダウン用の「当月を含み過去3ヶ月分」の選択肢を作成
        Map<String, String> monthOptions = new LinkedHashMap<>();
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("M月"); // 表示用（例：11月）

        for (int i = 0; i < 3; i++) {
            LocalDate targetMonth = currentMonth.minusMonths(i);
            // キーに "2026-11-01"、値に "11月" をセット
            monthOptions.put(targetMonth.toString(), targetMonth.format(labelFormatter));
        }

        // 表示対象の月を決定
        LocalDate selectedMonth = currentMonth;
        if (monthStr != null && monthOptions.containsKey(monthStr)) {
            selectedMonth = LocalDate.parse(monthStr);
        }

        // ログイン中のユーザー情報を取得して学習データを検索
        List<Item> items = new ArrayList<>();
        if (userDetails != null) {
            User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (user != null) {
                items = itemRepository.findByUserIdAndStudyDate(user.getId(), selectedMonth);
            }
        }

        // カテゴリーごとにデータを振り分ける
        List<Item> backendItems = items.stream()
            .filter(item -> "バックエンド".equals(item.getCategory()))
            .collect(Collectors.toList());

        List<Item> frontendItems = items.stream()
            .filter(item -> "フロントエンド".equals(item.getCategory()))
            .collect(Collectors.toList());

        List<Item> infraItems = items.stream()
            .filter(item -> "インフラ".equals(item.getCategory()))
            .collect(Collectors.toList());

        // 6. 画面（skill-edit.html）にデータを渡す
        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("selectedMonth", selectedMonth.toString());
        model.addAttribute("backendItems", backendItems);
        model.addAttribute("frontendItems", frontendItems);
        model.addAttribute("infraItems", infraItems);

        return "skill-edit";
    }

    @PostMapping("/skill-update")
    public String updateStudyTime(
        @RequestParam("id") Long id,
        @RequestParam("studyTime") Integer studyTime,
        @RequestParam("month") String month,
        RedirectAttributes redirectAttributes
    ) {
        // IDで対象の学習項目をDBから検索
        Item item = itemRepository.findById(id).orElse(null);

        // データが存在し、学習時間が入力されていれば更新して保存する
        if (item != null && studyTime != null) {
            item.setStudyTime(studyTime);
            itemRepository.save(item);

            // リダイレクト先に「更新完了モーダルを表示する合図」と「更新した項目名・時間」を渡す
            redirectAttributes.addFlashAttribute("isUpdated", true);
            redirectAttributes.addFlashAttribute("updatedItemName", item.getName());
            redirectAttributes.addFlashAttribute("updatedStudyTime", item.getStudyTime());
        }

        // 元々開いていた月（month）の一覧画面にリダイレクト
        return "redirect:/skill-edit?month=" + month;
    }
}