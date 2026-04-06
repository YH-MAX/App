-- =====================================================
-- SmartWater Community Platform - Twitter-Like Features
-- Database Migration Script
-- =====================================================

-- Run this script on your MySQL database to add Twitter-like features

-- 1. Add new columns to users table for profile features
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS bio VARCHAR(160) NULL,
ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500) NULL,
ADD COLUMN IF NOT EXISTS header_image_url VARCHAR(500) NULL,
ADD COLUMN IF NOT EXISTS follower_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS following_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS post_count INT DEFAULT 0;

-- 2. Add new columns to community_posts table for engagement
ALTER TABLE community_posts
ADD COLUMN IF NOT EXISTS retweet_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS view_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS bookmark_count INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS is_retweet BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS original_post_id BIGINT NULL;

-- 3. Create user_follows table for follow relationships
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_follow (follower_id, following_id),
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id)
);

-- 4. Create post_likes table for individual like tracking
CREATE TABLE IF NOT EXISTS post_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    UNIQUE KEY unique_like (user_id, post_id),
    INDEX idx_user_likes (user_id),
    INDEX idx_post_likes (post_id)
);

-- 5. Create post_bookmarks table for saved posts
CREATE TABLE IF NOT EXISTS post_bookmarks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    UNIQUE KEY unique_bookmark (user_id, post_id),
    INDEX idx_user_bookmarks (user_id),
    INDEX idx_post_bookmarks (post_id)
);

-- 6. Create post_retweets table for retweets and quote tweets
CREATE TABLE IF NOT EXISTS post_retweets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    original_post_id BIGINT NOT NULL,
    quote_content VARCHAR(280) NULL,
    is_quote_tweet BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (original_post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    INDEX idx_user_retweets (user_id),
    INDEX idx_post_retweets (original_post_id)
);

-- 7. Add index for content search
CREATE FULLTEXT INDEX IF NOT EXISTS idx_post_content ON community_posts(content);

-- =====================================================
-- End of Migration Script
-- =====================================================
