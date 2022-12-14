package spring.mvc.session09.controller;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.counting;
import static java.util.function.Function.identity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lotto")
public class LottoController {
	// 存放所有 lotto 紀錄的集合
	private List<Set<Integer>> lottos = new CopyOnWriteArrayList<>();

	// lotto 主畫面
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("lottos", lottos); // 歷史 lotto 紀錄
		return "session09/lotto";
	}

	// 取得最新電腦選號
	@GetMapping(value = { "/get", "/add" })
	public String get(Model model) {
		// 取得最新 lotto 資料
		Set<Integer> lotto = getRandomLotto();
		// 將 lotto 放入 lottos 歷史紀錄中
		lottos.add(lotto);
		// 要回傳給 jsp 的資料
		model.addAttribute("lotto", lotto); // 此次得到的 lotto 資料
		model.addAttribute("lottos", lottos); // 歷史 lotto 紀錄
		return "session09/lotto"; // jsp 所在位置
	}

	// 修改指定 index 的 lotto 紀錄. 注意: { index } 不可有空白
	@GetMapping("/update/{index}")
	public String update(@PathVariable("index") int index) {
		// 重新取得 lotto 號碼
		Set<Integer> lotto = getRandomLotto();
		// 將 lotto 放在指定 index 的位置 (更新資料)
		lottos.set(index, lotto);
		return "redirect:/mvc/lotto/"; // 重導到 lotto 主畫面
	}
	
	// 變更指定 [row][col] 的 lotto 紀錄
	@GetMapping("/change/{row}/{col}")
	public String change(@PathVariable("row") int row, @PathVariable("col") int col) {
		Set<Integer> curr = lottos.get(row); // Set
		List<Integer> list = new ArrayList<>(curr); // Set 轉 List
		
		while (true) {
			int n = new Random().nextInt(39) + 1;
			if(!curr.contains(n)) { // 若 curr 不包含 n
				list.set(col, n); // 變更資料
				break;
			}
		}
		
		curr = new LinkedHashSet<>(list); // List 轉 Set
		// 將 lotto 放在指定 index 的位置 (更新資料)
		lottos.set(row, curr);
		return "redirect:/mvc/lotto/"; // 重導到 lotto 主畫面
	}
	
	// 刪除指定 index 的 lotto 紀錄
	@GetMapping("/delete/{index}")
	public String delete(@PathVariable("index") int index) {
		// 根據 index 位置刪除該筆紀錄
		lottos.remove(index);
		return "redirect:/mvc/lotto/"; // 重導到 lotto 主畫面
	}
	
	// 統計每一個號碼出現的次數
	@GetMapping("/stat")
	public String stat(Model model) {
		// 1. 將所有的資料先利用 flatMap 拆散再透過 collect 收集起來
		List<Integer> nums = lottos.stream()
								   .parallel() // 平行運算
								   .flatMap(lotto -> lotto.stream()) // List<Integer> -> Stream<Integer>
								   .collect(toList()); // List<Integer>
		// 2. 透過 groupingBy 將資料分組
		Map<Integer, Long> stat = nums.stream()
									  .parallel() // 平行運算
									  .collect(groupingBy(identity(), counting()));
		
		// 3. 加上排序 (反向排序: 大 -> 小)
		Map<Integer, Long> result = new LinkedHashMap<>(); // 存放排序後的結果
		stat.entrySet().stream()
					   .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()) // 根據 value 反排序
					   .forEachOrdered(e -> result.put(e.getKey(), e.getValue())); // 將結果依序存放到 result 中
		stat = result; // 統計資料有排序
		model.addAttribute("stat", stat); // 統計資料
		model.addAttribute("lottos", lottos); // 歷史 lotto 紀錄
		return "session09/lotto";
	}
	
	// 隨機產生 lotto 電腦選號
	private Set<Integer> getRandomLotto() {
		// 1~39 取5個不重複的數字
		Random r = new Random();
		Set<Integer> lotto = new LinkedHashSet<>();
		while (lotto.size() < 5) {
			lotto.add(r.nextInt(39) + 1);
		}
		return lotto;
	}

}
