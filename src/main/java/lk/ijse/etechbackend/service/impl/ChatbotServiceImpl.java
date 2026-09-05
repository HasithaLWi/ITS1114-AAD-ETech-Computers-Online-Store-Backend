package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.chat.ChatMessageRequestDTO;
import lk.ijse.etechbackend.dto.chat.ChatMessageResponseDTO;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotServiceImpl implements ChatbotService {

    private final ProductRepository productRepository;

    @Override
    public ChatMessageResponseDTO processMessage(ChatMessageRequestDTO request) {
        String msg = request.getMessage() != null ? request.getMessage().toLowerCase().trim() : "";
        log.info("Processing AI chatbot message: {}", msg);

        List<Product> allProducts = productRepository.findAll().stream()
                .filter(p -> p.getProductStatus() != null && p.getProductStatus() != Status.DELETED)
                .collect(Collectors.toList());
        List<Product> matchingProducts = new ArrayList<>();
        List<Long> suggestedIds = new ArrayList<>();
        StringBuilder replyBuilder = new StringBuilder();

        // Check intents
        if (msg.contains("shipping") || msg.contains("delivery") || msg.contains("fee") || msg.contains("rate")) {
            replyBuilder.append("📦 **ETech Express Logistics**: We offer same-day and next-day shipping from our 4 regional hubs in Colombo (BR-COL), Galle (BR-GAL), Matara (BR-MAT), and Kandy (BR-KAN). Base shipping starts at LKR 350.00 with accurate live distance calculation!");
        } else if (msg.contains("warranty") || msg.contains("guarantee") || msg.contains("claim")) {
            replyBuilder.append("🛡️ **Official Manufacturer Warranty**: All our hardware carries 100% genuine distributor warranties (2 to 3 years standard, lifetime for RAM). You can visit any of our service centers in Colombo, Galle, Matara, or Kandy for instant assistance.");
        } else if (msg.contains("return") || msg.contains("refund") || msg.contains("policy")) {
            replyBuilder.append("📋 **Returns & Guarantee Policy**: We offer a 7-day hassle-free replacement guarantee for defective units and standard manufacturer warranty fulfillment. All products are verified authentic with intact serial tracking.");
        } else {
            // Product recommendations search
            for (Product p : allProducts) {
                if (p.getProductStatus() == Status.DELETED) continue;
                String name = p.getName().toLowerCase();
                String desc = (p.getDescription() != null ? p.getDescription() : "").toLowerCase();
                String category = (p.getCategory() != null ? p.getCategory().getName() : "").toLowerCase();
                String brand = (p.getBrand() != null ? p.getBrand().getName() : "").toLowerCase();

                boolean match = false;
                if (msg.contains("laptop") && (category.contains("laptop") || name.contains("laptop") || name.contains("scar") || name.contains("titan"))) match = true;
                else if ((msg.contains("gpu") || msg.contains("graphics") || msg.contains("rtx") || msg.contains("4070") || msg.contains("4090")) && (name.contains("rtx") || name.contains("gpu"))) match = true;
                else if ((msg.contains("keyboard") || msg.contains("key")) && (name.contains("keyboard") || name.contains("k70") || name.contains("huntsman"))) match = true;
                else if ((msg.contains("mouse") || msg.contains("mice")) && (name.contains("mouse") || name.contains("superlight") || name.contains("pro x"))) match = true;
                else if ((msg.contains("monitor") || msg.contains("display") || msg.contains("screen") || msg.contains("oled")) && (category.contains("monitor") || name.contains("oled") || name.contains("swift"))) match = true;
                else if ((msg.contains("ram") || msg.contains("memory") || msg.contains("ddr5")) && (name.contains("ram") || name.contains("dominator") || name.contains("ddr5"))) match = true;
                else if (msg.contains("asus") && (brand.contains("asus") || name.contains("asus") || name.contains("rog"))) match = true;
                else if (msg.contains("msi") && (brand.contains("msi") || name.contains("msi"))) match = true;
                else if (msg.contains("corsair") && (brand.contains("corsair") || name.contains("corsair"))) match = true;
                else if (msg.contains("logitech") && (brand.contains("logitech") || name.contains("logitech"))) match = true;
                else if (msg.contains("razer") && (brand.contains("razer") || name.contains("razer"))) match = true;

                if (match) {
                    matchingProducts.add(p);
                    suggestedIds.add(p.getId());
                }
            }

            if (!matchingProducts.isEmpty()) {
                replyBuilder.append("⚡ **Top Recommendations for You**:\n\n");
                for (int i = 0; i < Math.min(3, matchingProducts.size()); i++) {
                    Product p = matchingProducts.get(i);
                    replyBuilder.append(String.format("• **%s** — LKR %,.2f (Rating: ⭐ %s)\n  _%s_\n\n",
                            p.getName(), p.getPrice(), p.getRating(), p.getDescription()));
                }
                replyBuilder.append("Would you like more technical specifications or help adding any of these items to your cart?");
            } else {
                replyBuilder.append("👋 Hello! I am your **ETech AI Hardware Specialist**. I can help you find high-performance gaming laptops, GPUs (RTX 40-Series), mechanical keyboards, esports mice, OLED gaming monitors, and check real-time stock across our Colombo, Galle, Matara, and Kandy hubs. What are you building today?");
            }
        }

        return ChatMessageResponseDTO.builder()
                .reply(replyBuilder.toString())
                .suggestedProducts(suggestedIds.stream().limit(5).collect(Collectors.toList()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
