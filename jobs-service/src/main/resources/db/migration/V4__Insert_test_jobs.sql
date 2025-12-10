-- Jobs Service - Test Job Listings
-- Migration: V4__Insert_test_jobs.sql
-- Description: 100+ realistic job listings across Turkey

-- Technology Jobs - Istanbul
INSERT INTO job_listings (id, owner_id, owner_type, title, description, category, salary_min, salary_max, salary_currency, location, city, is_remote, job_type, experience_level, required_skills, status, published_at, application_deadline, is_active, created_at, updated_at) VALUES

-- Senior Positions
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Senior Full Stack Developer', 'React, Node.js, PostgreSQL expertise required. Microservices architecture experience preferred. Join our growing fintech team.', 'Technology', 45000, 65000, 'TRY', 'Maslak, Sarıyer', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'React,Node.js,PostgreSQL,Docker,Kubernetes', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Senior DevOps Engineer', 'AWS/Azure experience, CI/CD pipelines, Terraform. Leading cloud infrastructure projects.', 'Technology', 50000, 70000, 'TRY', 'Levent, Beşiktaş', 'Istanbul', true, 'FULL_TIME', 'SENIOR', 'AWS,Docker,Kubernetes,Terraform,Jenkins', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Lead Mobile Developer (iOS/Android)', 'Native mobile development leadership role. Flutter experience is a plus.', 'Technology', 55000, 75000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'LEAD', 'Swift,Kotlin,Flutter,CI/CD,Team Leadership', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Data Scientist - AI/ML', 'Python, TensorFlow, PyTorch. Building recommendation systems for e-commerce platform.', 'Technology', 48000, 68000, 'TRY', 'Ataşehir', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Python,TensorFlow,PyTorch,SQL,Data Analysis', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Backend Developer - Java/Spring', 'Microservices, Spring Boot, Kafka. Banking domain experience preferred.', 'Technology', 42000, 60000, 'TRY', 'Şişli', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Java,Spring Boot,Kafka,Microservices,PostgreSQL', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Mid-Level Positions
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Frontend Developer - React', 'Building modern web applications. TypeScript and Next.js experience required.', 'Technology', 30000, 42000, 'TRY', 'Kadıköy', 'Istanbul', true, 'FULL_TIME', 'MID', 'React,TypeScript,Next.js,Redux,CSS', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Backend Developer - Python/Django', 'RESTful API development, Django ORM, Celery for async tasks.', 'Technology', 28000, 40000, 'TRY', 'Beşiktaş', 'Istanbul', true, 'FULL_TIME', 'MID', 'Python,Django,REST API,PostgreSQL,Redis', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Mobile Developer - Flutter', 'Cross-platform mobile development. State management expertise required.', 'Technology', 32000, 45000, 'TRY', 'Üsküdar', 'Istanbul', false, 'FULL_TIME', 'MID', 'Flutter,Dart,State Management,REST API,Firebase', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'QA Automation Engineer', 'Selenium, Cypress, API testing. CI/CD integration experience.', 'Technology', 26000, 38000, 'TRY', 'Sarıyer', 'Istanbul', false, 'FULL_TIME', 'MID', 'Selenium,Cypress,Jest,API Testing,CI/CD', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Database Administrator - PostgreSQL', 'Database optimization, backup strategies, replication setup.', 'Technology', 30000, 42000, 'TRY', 'Şişli', 'Istanbul', false, 'FULL_TIME', 'MID', 'PostgreSQL,SQL,Database Optimization,Backup,Monitoring', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Entry Level / Junior
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Junior Full Stack Developer', 'Learn React and Node.js in a mentorship program. Fresh graduates welcome.', 'Technology', 18000, 25000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'ENTRY', 'JavaScript,HTML,CSS,Basic React,Git', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Junior Data Analyst', 'SQL, Excel, basic Python. Data visualization with Tableau/PowerBI.', 'Technology', 16000, 23000, 'TRY', 'Beşiktaş', 'Istanbul', false, 'FULL_TIME', 'ENTRY', 'SQL,Excel,Python,Data Visualization,Statistics', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Frontend Developer Intern', '6-month paid internship. Learn modern web development.', 'Technology', 10000, 15000, 'TRY', 'Kadıköy', 'Istanbul', false, 'INTERNSHIP', 'ENTRY', 'HTML,CSS,JavaScript,Basic React,Git', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Remote Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Remote Full Stack Developer', 'Work from anywhere in Turkey. Flexible hours. Modern tech stack.', 'Technology', 35000, 50000, 'TRY', 'Remote', 'Istanbul', true, 'FULL_TIME', 'MID', 'React,Node.js,MongoDB,AWS,Docker', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Remote Backend Developer - Go', 'Golang microservices. 100% remote position. International team.', 'Technology', 40000, 55000, 'TRY', 'Remote', 'Istanbul', true, 'FULL_TIME', 'MID', 'Go,Microservices,gRPC,PostgreSQL,Docker', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Technology Jobs - Ankara
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Senior Software Architect', 'Design scalable systems. Government projects experience preferred.', 'Technology', 48000, 65000, 'TRY', 'Çankaya', 'Ankara', false, 'FULL_TIME', 'LEAD', 'System Design,Microservices,Cloud,Team Leadership', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Cybersecurity Specialist', 'Network security, penetration testing, security audits.', 'Technology', 35000, 50000, 'TRY', 'Kızılay', 'Ankara', false, 'FULL_TIME', 'MID', 'Network Security,Penetration Testing,Firewall,SIEM', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Backend Developer - .NET Core', '.NET Core, Entity Framework, SQL Server. Enterprise applications.', 'Technology', 28000, 40000, 'TRY', 'Bilkent', 'Ankara', false, 'FULL_TIME', 'MID', '.NET Core,C#,SQL Server,Entity Framework,REST API', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'IT Support Specialist', 'Hardware/software support, network troubleshooting, user training.', 'Technology', 15000, 22000, 'TRY', 'Ulus', 'Ankara', false, 'FULL_TIME', 'ENTRY', 'Windows,Linux,Network,Hardware,Help Desk', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Technology Jobs - Izmir
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Full Stack Developer - MERN Stack', 'MongoDB, Express, React, Node.js. E-commerce platform development.', 'Technology', 30000, 42000, 'TRY', 'Alsancak', 'Izmir', false, 'FULL_TIME', 'MID', 'MongoDB,Express,React,Node.js,AWS', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Mobile App Developer - React Native', 'Cross-platform mobile apps. Healthcare domain.', 'Technology', 28000, 38000, 'TRY', 'Bornova', 'Izmir', false, 'FULL_TIME', 'MID', 'React Native,JavaScript,REST API,Redux,Firebase', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Junior Software Developer', 'Learn and grow with experienced team. Any language background.', 'Technology', 16000, 24000, 'TRY', 'Konak', 'Izmir', false, 'FULL_TIME', 'ENTRY', 'Programming,Git,Problem Solving,Team Work', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Marketing Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Digital Marketing Manager', 'SEO, SEM, social media marketing. Lead generation campaigns.', 'Marketing', 28000, 40000, 'TRY', 'Nişantaşı', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'SEO,SEM,Google Ads,Social Media,Analytics', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Social Media Specialist', 'Content creation, Instagram/TikTok marketing, influencer collaborations.', 'Marketing', 18000, 26000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'MID', 'Social Media,Content Creation,Analytics,Copywriting', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Content Marketing Writer', 'Blog posts, newsletters, case studies. SEO writing expertise.', 'Marketing', 16000, 24000, 'TRY', 'Beşiktaş', 'Istanbul', true, 'FULL_TIME', 'MID', 'Content Writing,SEO,Copywriting,WordPress,Research', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Growth Marketing Specialist', 'A/B testing, conversion optimization, user acquisition strategies.', 'Marketing', 25000, 36000, 'TRY', 'Maslak', 'Istanbul', false, 'FULL_TIME', 'MID', 'Growth Hacking,Analytics,A/B Testing,SQL,Product Sense', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Email Marketing Specialist', 'Campaign management, automation, segmentation. Mailchimp/Sendgrid.', 'Marketing', 17000, 25000, 'TRY', 'Şişli', 'Istanbul', true, 'FULL_TIME', 'MID', 'Email Marketing,Mailchimp,Automation,Analytics,Copywriting', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Sales Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Enterprise Sales Manager', 'B2B software sales. SaaS experience required. High commission potential.', 'Sales', 30000, 50000, 'TRY', 'Levent', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'B2B Sales,SaaS,Negotiation,CRM,Presentation', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Sales Representative - Retail', 'Electronics store sales. Product knowledge training provided.', 'Sales', 14000, 20000, 'TRY', 'Bağdat Caddesi', 'Istanbul', false, 'FULL_TIME', 'ENTRY', 'Customer Service,Sales,Communication,Product Knowledge', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Inside Sales Executive', 'Outbound calls, lead qualification, CRM management. Remote possible.', 'Sales', 16000, 25000, 'TRY', 'Kadıköy', 'Istanbul', true, 'FULL_TIME', 'MID', 'Sales,Cold Calling,CRM,Communication,Persistence', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Account Manager', 'Client relationship management. Upselling and retention focus.', 'Sales', 22000, 32000, 'TRY', 'Beşiktaş', 'Istanbul', false, 'FULL_TIME', 'MID', 'Account Management,Sales,Negotiation,CRM,Analytics', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Business Development Manager', 'New market opportunities, partnerships, strategic growth.', 'Sales', 28000, 42000, 'TRY', 'Maslak', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Business Development,Strategy,Negotiation,Networking', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Design Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Senior UI/UX Designer', 'Mobile app design, user research, prototyping. Figma expert.', 'Design', 30000, 45000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'UI/UX,Figma,User Research,Prototyping,Design Systems', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Graphic Designer', 'Social media graphics, brand materials, print design.', 'Design', 18000, 28000, 'TRY', 'Beyoğlu', 'Istanbul', false, 'FULL_TIME', 'MID', 'Photoshop,Illustrator,InDesign,Branding,Typography', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Product Designer', 'End-to-end product design. Work closely with engineering team.', 'Design', 26000, 38000, 'TRY', 'Beşiktaş', 'Istanbul', false, 'FULL_TIME', 'MID', 'Product Design,UI/UX,Figma,User Research,Prototyping', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Motion Graphics Designer', 'Video animations, explainer videos, social media content.', 'Design', 20000, 30000, 'TRY', 'Şişli', 'Istanbul', false, 'FULL_TIME', 'MID', 'After Effects,Premiere,Animation,Video Editing,3D', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Finance Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Financial Analyst', 'Financial modeling, forecasting, reporting. MBA preferred.', 'Finance', 28000, 40000, 'TRY', 'Levent', 'Istanbul', false, 'FULL_TIME', 'MID', 'Financial Analysis,Excel,Modeling,SQL,Reporting', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Accounting Specialist', 'General ledger, accounts payable/receivable, month-end close.', 'Finance', 16000, 24000, 'TRY', 'Şişli', 'Istanbul', false, 'FULL_TIME', 'MID', 'Accounting,ERP,Excel,Financial Reporting,Tax', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Budget Analyst', 'Budget planning, variance analysis, cost control.', 'Finance', 18000, 26000, 'TRY', 'Maslak', 'Istanbul', false, 'FULL_TIME', 'MID', 'Budgeting,Excel,Financial Analysis,Reporting,ERP', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Tax Specialist', 'Corporate tax compliance, tax planning, audit support.', 'Finance', 22000, 32000, 'TRY', 'Levent', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Tax,Accounting,Compliance,Audit,Financial Reporting', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Healthcare Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Registered Nurse', 'Patient care, medication administration, health monitoring.', 'Healthcare', 20000, 28000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'MID', 'Nursing,Patient Care,Medical Knowledge,First Aid', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Pharmacist', 'Medication dispensing, patient counseling, inventory management.', 'Healthcare', 18000, 26000, 'TRY', 'Nişantaşı', 'Istanbul', false, 'FULL_TIME', 'MID', 'Pharmacy,Medication,Patient Counseling,Inventory', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Medical Lab Technician', 'Laboratory testing, equipment operation, quality control.', 'Healthcare', 14000, 20000, 'TRY', 'Bakırköy', 'Istanbul', false, 'FULL_TIME', 'MID', 'Lab Testing,Medical Equipment,Quality Control,Safety', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Physical Therapist', 'Rehabilitation programs, patient assessments, treatment planning.', 'Healthcare', 16000, 24000, 'TRY', 'Beşiktaş', 'Istanbul', false, 'FULL_TIME', 'MID', 'Physical Therapy,Rehabilitation,Patient Care,Assessment', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Education Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'English Teacher', 'Native or near-native fluency. TEFL/CELTA certification required.', 'Education', 18000, 26000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'MID', 'English Teaching,TEFL,Curriculum,Classroom Management', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Mathematics Teacher', 'High school mathematics. Experienced in IB curriculum preferred.', 'Education', 16000, 24000, 'TRY', 'Üsküdar', 'Istanbul', false, 'FULL_TIME', 'MID', 'Mathematics,Teaching,Curriculum,Assessment,IB', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Online Course Instructor - Coding', 'Create and teach coding courses. YouTube content creation.', 'Education', 20000, 30000, 'TRY', 'Remote', 'Istanbul', true, 'PART_TIME', 'MID', 'Programming,Teaching,Video Creation,Communication', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Academic Advisor', 'Student counseling, career guidance, university applications.', 'Education', 14000, 20000, 'TRY', 'Nişantaşı', 'Istanbul', false, 'FULL_TIME', 'MID', 'Counseling,Career Guidance,Communication,Problem Solving', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Other cities...
-- Ankara Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Project Manager', 'Agile/Scrum projects. Government IT projects experience.', 'Technology', 32000, 45000, 'TRY', 'Çankaya', 'Ankara', false, 'FULL_TIME', 'SENIOR', 'Project Management,Agile,Scrum,Stakeholder Management', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'HR Specialist', 'Recruitment, onboarding, employee relations, HR policies.', 'Other', 16000, 24000, 'TRY', 'Kızılay', 'Ankara', false, 'FULL_TIME', 'MID', 'HR,Recruitment,Employee Relations,Policy,Communication', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Executive Assistant', 'Calendar management, travel coordination, executive support.', 'Other', 14000, 20000, 'TRY', 'Çankaya', 'Ankara', false, 'FULL_TIME', 'MID', 'Office Management,Communication,Organization,MS Office', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Izmir Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Customer Success Manager', 'Client onboarding, retention, upselling. SaaS experience.', 'Sales', 22000, 32000, 'TRY', 'Alsancak', 'Izmir', false, 'FULL_TIME', 'MID', 'Customer Success,CRM,Communication,Problem Solving', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Operations Manager', 'Supply chain, logistics, process optimization.', 'Other', 26000, 38000, 'TRY', 'Karşıyaka', 'Izmir', false, 'FULL_TIME', 'SENIOR', 'Operations,Supply Chain,Logistics,Process Improvement', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Legal Counsel', 'Contract review, corporate law, compliance. Bar membership required.', 'Other', 28000, 40000, 'TRY', 'Konak', 'Izmir', false, 'FULL_TIME', 'SENIOR', 'Law,Contracts,Compliance,Negotiation,Corporate Law', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Antalya - Tourism Focus
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Hotel Manager', '5-star hotel management experience. Fluent English required.', 'Other', 24000, 35000, 'TRY', 'Lara', 'Antalya', false, 'FULL_TIME', 'SENIOR', 'Hotel Management,Hospitality,English,Team Leadership', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Tour Guide', 'Multiple languages. Historical sites expertise. Seasonal position.', 'Other', 12000, 18000, 'TRY', 'Kaleiçi', 'Antalya', false, 'PART_TIME', 'MID', 'Tour Guiding,History,Languages,Communication,Customer Service', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Restaurant Chef', 'Mediterranean cuisine. International menu experience.', 'Other', 18000, 26000, 'TRY', 'Konyaaltı', 'Antalya', false, 'FULL_TIME', 'MID', 'Cooking,Mediterranean Cuisine,Menu Planning,Kitchen Management', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Event Coordinator', 'Weddings, conferences, corporate events. Hotel venue experience.', 'Other', 16000, 24000, 'TRY', 'Belek', 'Antalya', false, 'FULL_TIME', 'MID', 'Event Planning,Coordination,Vendor Management,Communication', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bursa - Industrial
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Mechanical Engineer', 'Automotive industry. CAD/CAM expertise. Production line optimization.', 'Other', 24000, 34000, 'TRY', 'Osmangazi', 'Bursa', false, 'FULL_TIME', 'MID', 'Mechanical Engineering,CAD,Production,Quality Control', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Quality Control Engineer', 'ISO standards, Six Sigma. Manufacturing quality assurance.', 'Other', 18000, 26000, 'TRY', 'Nilüfer', 'Bursa', false, 'FULL_TIME', 'MID', 'Quality Control,ISO,Six Sigma,Manufacturing,Analysis', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Production Supervisor', 'Shift management, team leadership, production targets.', 'Other', 20000, 28000, 'TRY', 'Yıldırım', 'Bursa', false, 'FULL_TIME', 'MID', 'Production,Team Leadership,Manufacturing,Lean', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Adana
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Agricultural Engineer', 'Crop management, irrigation systems, sustainability.', 'Other', 16000, 24000, 'TRY', 'Seyhan', 'Adana', false, 'FULL_TIME', 'MID', 'Agriculture,Crop Management,Irrigation,Sustainability', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Logistics Coordinator', 'Transportation, warehouse management, shipment tracking.', 'Other', 14000, 20000, 'TRY', 'Çukurova', 'Adana', false, 'FULL_TIME', 'MID', 'Logistics,Transportation,Warehouse,Coordination,ERP', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Gaziantep
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Export Sales Manager', 'International sales, export documentation, customs procedures.', 'Sales', 22000, 32000, 'TRY', 'Şahinbey', 'Gaziantep', false, 'FULL_TIME', 'SENIOR', 'Export,Sales,International Trade,Customs,Languages', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Food Production Manager', 'Baklava/pastry production. Food safety, quality standards.', 'Other', 18000, 26000, 'TRY', 'Şehitkamil', 'Gaziantep', false, 'FULL_TIME', 'MID', 'Food Production,Quality Control,Safety,Team Management', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Trabzon
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Marine Biologist', 'Black Sea fisheries research. Academic background required.', 'Other', 16000, 24000, 'TRY', 'Ortahisar', 'Trabzon', false, 'FULL_TIME', 'MID', 'Marine Biology,Research,Data Analysis,Fieldwork', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Tea Factory Worker', 'Tea processing, quality control, packaging. Training provided.', 'Other', 12000, 16000, 'TRY', 'Arsin', 'Trabzon', false, 'FULL_TIME', 'ENTRY', 'Manufacturing,Quality Control,Physical Work,Team Work', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Contract/Freelance Jobs
(gen_random_uuid(), gen_random_uuid(), 'USER', 'Freelance Web Developer', '3-month contract. E-commerce website development.', 'Technology', 35000, 45000, 'TRY', 'Remote', 'Istanbul', true, 'CONTRACT', 'MID', 'React,Node.js,E-commerce,Payment Integration,SEO', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'USER', 'Freelance Graphic Designer', 'Branding project - 2 months. Logo, business cards, social media.', 'Design', 20000, 30000, 'TRY', 'Remote', 'Istanbul', true, 'CONTRACT', 'MID', 'Branding,Logo Design,Illustrator,Photoshop,Portfolio', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '20 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'USER', 'Freelance Content Writer - Tech Blog', 'Weekly articles. Long-term collaboration potential.', 'Marketing', 15000, 22000, 'TRY', 'Remote', 'Istanbul', true, 'CONTRACT', 'MID', 'Content Writing,Tech Knowledge,SEO,Research,English', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Part-time Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Part-time Social Media Manager', '20 hours/week. Instagram and TikTok focus.', 'Marketing', 8000, 12000, 'TRY', 'Kadıköy', 'Istanbul', false, 'PART_TIME', 'MID', 'Social Media,Content Creation,Analytics,Scheduling', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Part-time Barista', 'Weekend shifts. Coffee specialty knowledge preferred.', 'Other', 7000, 10000, 'TRY', 'Nişantaşı', 'Istanbul', false, 'PART_TIME', 'ENTRY', 'Coffee Making,Customer Service,Fast Paced,Team Work', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Part-time Tutor - Mathematics', 'High school students. Flexible hours, weekends available.', 'Education', 10000, 15000, 'TRY', 'Üsküdar', 'Istanbul', false, 'PART_TIME', 'MID', 'Mathematics,Teaching,Patience,Communication,Motivation', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Additional Remote Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Remote Customer Support Agent', 'Email and chat support. Night shift available with bonus.', 'Other', 14000, 20000, 'TRY', 'Remote', 'Istanbul', true, 'FULL_TIME', 'ENTRY', 'Customer Service,Communication,Problem Solving,CRM', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Remote Data Entry Specialist', 'Accurate and fast typing. Excel proficiency required.', 'Other', 12000, 16000, 'TRY', 'Remote', 'Istanbul', true, 'FULL_TIME', 'ENTRY', 'Data Entry,Excel,Accuracy,Attention to Detail,Typing', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Remote Virtual Assistant', 'Administrative tasks, scheduling, email management.', 'Other', 14000, 20000, 'TRY', 'Remote', 'Istanbul', true, 'PART_TIME', 'MID', 'Administration,Organization,Communication,MS Office,Time Management', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Additional Technology Jobs
(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Blockchain Developer', 'Smart contracts, Solidity, Web3. Crypto experience preferred.', 'Technology', 40000, 60000, 'TRY', 'Maslak', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Blockchain,Solidity,Web3,Smart Contracts,Ethereum', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Game Developer - Unity', 'Mobile games. 3D graphics, multiplayer experience.', 'Technology', 32000, 45000, 'TRY', 'Kadıköy', 'Istanbul', false, 'FULL_TIME', 'MID', 'Unity,C#,Game Development,3D,Multiplayer', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'AR/VR Developer', 'Augmented reality applications. Unity/Unreal Engine.', 'Technology', 35000, 50000, 'TRY', 'Beşiktaş', 'Istanbul', false, 'FULL_TIME', 'MID', 'AR/VR,Unity,Unreal Engine,3D,Mobile Development', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), gen_random_uuid(), 'SHOP', 'Machine Learning Engineer', 'NLP, computer vision. Production ML systems experience.', 'Technology', 42000, 58000, 'TRY', 'Maslak', 'Istanbul', false, 'FULL_TIME', 'SENIOR', 'Machine Learning,Python,TensorFlow,NLP,Computer Vision', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

