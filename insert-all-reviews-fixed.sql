-- Tüm ürünler için review ekleme script'i
DO $$
DECLARE
    product_ids UUID[] := ARRAY[
        '51ecf5af-ee6e-4eb7-9353-8a00beb948e7'::UUID,
        'bd04918a-6859-4586-9078-792c52d373ad'::UUID,
        'ef28ff4d-351b-498d-9827-66d101f980da'::UUID,
        '5cde000d-af21-4966-8d6e-8cd25a0acd32'::UUID,
        '2c0e2b64-4701-418d-ba36-af5e7004a69b'::UUID,
        '07f06000-2607-42e4-89bd-1c20ef3f68d6'::UUID,
        '33d96339-3e91-4b70-8f52-2904e5b35a1b'::UUID,
        '5fa50d0d-bddb-4a80-ae3a-be4e0f2c169f'::UUID,
        'a96bbff9-48ee-4b20-b799-867a0d32a6db'::UUID,
        '7f129b08-2795-4fd0-96bf-09bfe5681718'::UUID,
        'a3776f32-0a58-43c4-950e-0dc5126d5573'::UUID,
        '87ee543d-4f87-43e5-99d4-317df3f3c545'::UUID,
        '7b2fafcf-c3c8-44b5-ac39-d3824ce94770'::UUID,
        '92ec68d9-c9c3-4b8b-ab82-a290af58a874'::UUID,
        '87a56ad4-97dc-4d8a-841b-c6d08bd10954'::UUID,
        '281c16ac-371a-4a6d-b0fe-1c1ba46fd374'::UUID,
        '97c97a2b-8914-42e6-a277-81ae582e501a'::UUID,
        '1d9e0f90-07e8-46c5-b4e7-b4dc23ccfb03'::UUID,
        '357a181d-4fd4-47c0-a4b2-0c3b3cff66eb'::UUID,
        'f8a9fb6e-cfec-49b4-9f7e-5996291e354f'::UUID,
        '5e457465-576b-4142-b6e7-533810b578ee'::UUID,
        '01fc0c69-de6e-435e-8b36-61369b21075c'::UUID,
        '3a2a55eb-51cc-4b00-a246-8633cf8d9f0a'::UUID,
        '8fd8c3a5-6572-45a3-aff3-8941223447ab'::UUID,
        'ae81c0d1-3454-4492-bc6c-86d9706559c4'::UUID,
        'af98b531-3ff3-42fc-ae7e-fc2de0e465c6'::UUID,
        '1de31d6e-5c17-457f-9140-5b4d88b0aa89'::UUID,
        'f97b647e-8770-45da-9703-7b8ee58ab018'::UUID,
        '3bdcbcd8-0c34-4843-8db6-b9381ef3e450'::UUID,
        'e4975c97-3f2e-46e6-b15d-4e6344e43ba8'::UUID,
        '8cbfc445-a144-4fa1-b803-81303c9c2ed9'::UUID,
        '948f139f-a26c-4b98-abd7-280a1d688cb4'::UUID,
        '24bffa50-e7f3-427a-86ae-4dfd8e5f8641'::UUID,
        '47ef8aa1-4721-4d0e-8b37-45c7a909deb1'::UUID,
        'cc3a8121-9789-4396-a159-5a57e4238e28'::UUID,
        '568f9911-96fc-4493-a867-6c2291a14558'::UUID,
        '0c886b5f-62e8-4e3c-8b94-7cda94fd1400'::UUID,
        '991fe7c2-9a07-4f05-96b1-cc9ddaac381b'::UUID,
        '4d36d1d5-f77c-4859-a860-5fb0b8c7a23c'::UUID,
        '186cd48a-eb1a-4f65-9c79-efdc08930f59'::UUID,
        '10ec3d8a-e49a-4233-a55f-4c0434dcd767'::UUID,
        '9372bc69-2f0c-491b-9102-be7f1d750916'::UUID,
        'ed57785f-e276-46b3-a88d-b0df3a81c43b'::UUID,
        '2a5b0ca5-e089-49db-934e-66f2def7a83e'::UUID,
        'f58d3c5f-d574-4531-b24b-6c30f774d410'::UUID,
        '6b6ea1f8-8674-4bb2-bbd5-a285be107a5d'::UUID,
        '62e27410-cdc9-4e5e-b66f-f7f912cc25c3'::UUID,
        '9b8ab8b5-0677-45c1-a253-1e330b893bc4'::UUID,
        '35509e61-e2f6-413d-a4e8-d77748b0bd12'::UUID,
        '9601c1ec-79eb-41a6-9467-459498df0c43'::UUID,
        'fa9bc03e-6198-4604-8f89-d94eca4ec300'::UUID,
        'e7b0780f-a652-4057-9dcf-0336dcc31fa0'::UUID,
        '9d6f2860-d2be-4879-9864-72121724f6f2'::UUID,
        'f5922872-262f-4560-90b2-aa90411b135c'::UUID,
        '723fbef0-58c8-483d-aa08-627199b7078d'::UUID,
        '1a840a12-d2b6-46e9-8e5e-83fff7d872ea'::UUID,
        'f3155487-0af0-47cf-a3a6-5acc209bc43a'::UUID,
        'e261a332-161a-401b-8dc8-6be40d295f94'::UUID,
        '83eaf934-0b6b-495d-9ca9-30209114648c'::UUID,
        '074383d4-7259-469e-ba03-24116aed1c0b'::UUID,
        'f99e7b0c-e7fb-4241-a408-f5f5af896caa'::UUID,
        '8e4775c5-8d03-40fe-a787-e523cc3ad0e5'::UUID,
        '4df6fd71-9c38-4cad-8ae8-03879dd6988e'::UUID,
        '98c7a558-5776-42c2-a703-01eda4d14f39'::UUID,
        'ba432f97-ff61-4690-9ec7-ff7df7c8ac5c'::UUID,
        'e88f15e2-1081-4de1-ad83-de2d78f6ad20'::UUID,
        'f18ee163-c02a-4bf7-bdaa-133817fb0033'::UUID,
        '916e381c-0e55-4e45-83f7-06a4bde862a7'::UUID,
        'b66eee8d-3112-455f-8120-29c0fa47de06'::UUID,
        '57bb1451-d63d-4daa-8d23-8fb315f6da21'::UUID,
        'c373a688-3742-4fc5-8ef2-d30c780b8a70'::UUID,
        '353929a3-aa59-498c-ad53-f3fcd5a3aa99'::UUID,
        '23032f7b-aa99-4185-8926-370e587c77dc'::UUID,
        '9719807a-5867-4c56-97e8-c56bacae33e8'::UUID,
        'f2b55d09-168f-4920-b4a5-e634569c6632'::UUID,
        '9c04fa22-8386-4550-9c04-ceaf4eb375c1'::UUID,
        '86651487-863f-4dd8-9897-9f047187f8c9'::UUID,
        '984c5874-8a2f-4cec-8865-ef5e9ec872d0'::UUID,
        'f9787338-0c33-48e6-a519-2276afbc6db2'::UUID,
        'e495151b-698d-4f90-b14a-4675fbf634a7'::UUID,
        '248ea526-e3f4-4e2d-a796-0444c251c6f6'::UUID,
        '0ff3e7db-4354-4c75-b9a7-eb83937b9adb'::UUID,
        'acbb0e88-6888-4181-b65d-afe0b0b2ea5e'::UUID,
        'e31ac9e8-6e85-4a06-ac61-a7df5c8cdeaa'::UUID,
        '98234c71-50fd-4581-bcbc-4f878c04c008'::UUID,
        '910f27ff-2cd2-4f75-ad95-b3d01fee8228'::UUID,
        'e576e750-a9a0-4282-8f58-a32fb88cbc17'::UUID,
        '760afe67-e7eb-4aa5-b71f-327784dedf58'::UUID,
        '92004327-dd8c-4741-a35e-918716941097'::UUID,
        '85df710a-dd7e-40c9-9d85-6a76a96b93a8'::UUID,
        'b1c98b0f-014b-4067-8927-d89d922f7ded'::UUID,
        '1afd3838-1f12-40d2-83c5-d8123d4eff76'::UUID,
        'e1802bfa-b91e-41ad-869b-9879b0821a73'::UUID,
        '691ff254-93c8-48f1-9ec2-fa5e4063a95a'::UUID,
        'f2f8bbf1-0779-4396-bc7a-89d02404204f'::UUID,
        '0081ab78-25c2-4bb6-9e2b-8f0dcb02c9fd'::UUID,
        '00e3c2a7-ce3e-4009-a792-8e73a6479566'::UUID,
        'd1092c12-9d70-4708-a312-b77dc100efde'::UUID,
        'a3d30014-f31d-4f91-8fb8-a1459d008bcc'::UUID,
        '385898fe-79f0-498f-9fc1-957902ba3dd1'::UUID
    ];
    user_ids UUID[] := ARRAY[
        '550e8400-e29b-41d4-a716-446655440001'::UUID,
        '550e8400-e29b-41d4-a716-446655440002'::UUID,
        '550e8400-e29b-41d4-a716-446655440003'::UUID,
        '550e8400-e29b-41d4-a716-446655440004'::UUID,
        '550e8400-e29b-41d4-a716-446655440005'::UUID
    ];
    user_names TEXT[] := ARRAY['Admin User', 'John Doe', 'Jane Smith', 'Premium Customer', 'Test User'];
    positive_comments TEXT[] := ARRAY[
        'Harika bir ürün! Çok memnun kaldım, kesinlikle tavsiye ederim.',
        'Beklediğimden çok daha iyi çıktı. Kalite ve performans mükemmel.',
        'Çok beğendim, herkese öneririm. Fiyatına göre çok değerli.',
        'Mükemmel kalite, uzun süre kullanacağım. Çok memnun kaldım.',
        'Tasarımı çok şık ve kullanışlı. Beklentilerimi aştı.'
    ];
    neutral_comments TEXT[] := ARRAY[
        'Ürün iyi ama fiyat biraz yüksek. Yine de memnunum.',
        'Güzel ürün, memnunum. Belki biraz daha uygun fiyatlı olabilirdi.'
    ];
    negative_comments TEXT[] := ARRAY[
        'Ürün beklentilerimi karşılamadı. Kalite düşük geldi.',
        'Fiyatına göre kalite düşük. Memnun kalmadım.'
    ];
    current_product_id UUID;
    review_count INTEGER;
    comment_count INTEGER;
    i INTEGER;
    user_index INTEGER;
    selected_user_id UUID;
    selected_user_name TEXT;
    selected_rating INTEGER;
    selected_comment TEXT;
    days_ago INTEGER;
    days_ago_str TEXT;
BEGIN
    FOREACH current_product_id IN ARRAY product_ids
    LOOP
        review_count := 5 + floor(random() * 11)::INTEGER;
        comment_count := 5 + floor(random() * 6)::INTEGER;
        
        FOR i IN 1..review_count LOOP
            user_index := 1 + (i % array_length(user_ids, 1));
            selected_user_id := user_ids[user_index];
            selected_user_name := user_names[user_index] || ' ' || i::TEXT;
            
            IF random() < 0.6 THEN
                selected_rating := 4 + floor(random() * 2)::INTEGER;
            ELSIF random() < 0.9 THEN
                selected_rating := 3;
            ELSE
                selected_rating := 1 + floor(random() * 2)::INTEGER;
            END IF;
            
            IF i <= comment_count THEN
                IF selected_rating >= 4 THEN
                    selected_comment := positive_comments[1 + floor(random() * array_length(positive_comments, 1))::INTEGER];
                ELSIF selected_rating = 3 THEN
                    selected_comment := neutral_comments[1 + floor(random() * array_length(neutral_comments, 1))::INTEGER];
                ELSE
                    selected_comment := negative_comments[1 + floor(random() * array_length(negative_comments, 1))::INTEGER];
                END IF;
            ELSE
                selected_comment := NULL;
            END IF;
            
            days_ago := 1 + floor(random() * 60)::INTEGER;
            days_ago_str := days_ago::TEXT;
            
            INSERT INTO reviews (
                id, product_id, user_id, user_name, rating, comment, 
                is_approved, helpful_count, created_at, updated_at
            ) VALUES (
                gen_random_uuid(),
                current_product_id,
                selected_user_id,
                selected_user_name,
                selected_rating,
                selected_comment,
                true,
                floor(random() * 50)::INTEGER,
                CURRENT_TIMESTAMP - (days_ago_str || ' days')::INTERVAL,
                CURRENT_TIMESTAMP - (days_ago_str || ' days')::INTERVAL
            )
            ON CONFLICT (product_id, user_id) DO NOTHING;
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Review ekleme tamamlandı!';
END $$;

SELECT 
    COUNT(*) as total_reviews,
    COUNT(DISTINCT product_id) as products_with_reviews,
    COUNT(comment) as reviews_with_comments,
    ROUND(AVG(rating), 2) as average_rating
FROM reviews;

