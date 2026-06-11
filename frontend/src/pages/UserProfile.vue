<template>
  <div class="user-profile">
    <el-page-header @back="() => { goBack(); playSound('点击音效1'); }" content="个人中心" />
    
    <el-card class="profile-card">
      <div class="profile-header">
        <div class="avatar-container">
          <div class="avatar" @click="() => { changeAvatar(); playSound('点击音效2'); }">
            <img :src="userAvatar" :alt="user.username" />
            <div class="avatar-overlay">
              <span>更换头像</span>
            </div>
          </div>
          <!-- 性别符号 -->
          <div class="gender-symbol" v-if="profileForm.gender">
            <div v-if="profileForm.gender === 'male'" class="gender-male">♂</div>
            <div v-else-if="profileForm.gender === 'female'" class="gender-female">♀</div>
            <div v-else-if="profileForm.gender === 'secret'" class="gender-secret">?</div>
          </div>
        </div>
        <div class="user-info">
          <h2 class="username" @click="playSound('点击音效1')">{{ user.username }}</h2>
          <div class="user-bio" v-if="profileForm.bio">
            {{ profileForm.bio }}
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ user.level }}</span>
              <span class="stat-label">等级</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.experience }}</span>
              <span class="stat-label">经验</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ user.points }}</span>
              <span class="stat-label">积分</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>


    <el-card class="profile-content" :class="{ 'achievements-tab-active': activeTab === 'achievements' }">
      <template #header>
        <el-tabs v-model="activeTab" @tab-click="() => { playSound('点击音效1'); }">
          <el-tab-pane label="个人资料" name="profile">
            <div class="profile-edit">
              <el-form :model="profileForm" label-width="120px" class="profile-form">
                <el-form-item label="个人简介">
                  <el-input
                    v-model="profileForm.bio"
                    type="textarea"
                    placeholder="介绍一下自己吧"
                    :rows="3"
                    @focus="playClickSound"
                  />
                </el-form-item>

                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" placeholder="请输入用户名" @focus="playClickSound" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" @focus="playClickSound" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-select 
                    v-model="profileForm.gender" 
                    placeholder="请选择性别"
                    style="background-color: #2a2a2a !important; border-radius: 15px !important; box-shadow: 0 0 8px rgba(64, 196, 255, 0.2) !important;"
                    @focus="playClickSound"
                  >
                    <el-option label="男" value="male" />
                    <el-option label="女" value="female" />
                    <el-option label="保密" value="secret" />
                  </el-select>
                </el-form-item>
                <el-form-item label="生日">
                  <el-date-picker
                    v-model="profileForm.birthday"
                    type="date"
                    placeholder="选择生日"
                    style="width: 100%"
                    format="YYYY年MM月DD日"
                    value-format="YYYY-MM-DD"
                    :locale="zhCn"
                    @focus="playClickSound"
                  />
                </el-form-item>
                <el-form-item label="兴趣标签">
                  <el-tag
                    v-for="tag in profileForm.tags"
                    :key="tag"
                    closable
                    @close="() => { removeTag(tag); playSound('点击音效3'); }"
                    class="tag-item"
                  >
                    {{ tag }}
                  </el-tag>
                  <el-input
                    v-model="newTag"
                    @keyup.enter="addTag"
                    placeholder="输入标签后按回车添加"
                    style="width: 100px; margin-left: 10px;"
                    @focus="playClickSound"
                  />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" @focus="playClickSound" />
                </el-form-item>
                <el-form-item label="登录密码">
                  <el-input v-model="profileForm.password" type="password" placeholder="请输入新密码（留空表示不修改）" show-password @focus="playClickSound" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="() => { saveProfile(); playSound('注册音'); }" class="save-button">保存</el-button>
                <el-button type="primary" @click="() => { resetForm(); playSound('点击音效3'); }" class="reset-button">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          <el-tab-pane label="学习进度" name="progress">
            <div class="learning-progress">
              <el-table :data="paginatedLearningProgress" style="width: 100%" :row-style="{ height: '70px' }">
                <el-table-column prop="courseTitle" label="课程" min-width="200">
                  <template #default="scope">
                    <div class="course-title-cell">
                      {{ scope.row.courseTitle }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="progress" label="学习进度" min-width="350">
                  <template #default="scope">
                    <div class="progress-cell">
                      <el-progress 
                        :percentage="scope.row.progress" 
                        :color="getProgressColor(scope.row.progress)" 
                        :stroke-width="8"
                      />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="lastLearnTime" label="最近学习" min-width="180">
                  <template #default="scope">
                    <div class="time-cell">
                      {{ scope.row.lastLearnTime }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" min-width="120">
                  <template #default="scope">
                    <div class="action-cell">
                      <el-button 
                        type="primary" 
                        size="small" 
                        round
                        @click="() => { continueLearning(scope.row.courseId); playSound('点击音效1'); }"
                      >
                        继续学习
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="!learningProgress || learningProgress.length === 0" class="empty-state">
                <el-empty description="暂无学习进度" />
              </div>
              <div v-else class="pagination-container">
                <el-pagination
                  v-model:current-page="learningProgressCurrentPage"
                  v-model:page-size="learningProgressPageSize"
                  :page-sizes="[5, 10, 20, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="learningProgress.length"
                  @size-change="handleLearningProgressSizeChange"
                  @current-change="handleLearningProgressCurrentChange"
                />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="我的收藏" name="collections">
            <div class="collections">
              <el-row :gutter="20">
                <el-col 
                  v-for="course in paginatedCollections" 
                  :key="course.id" 
                  :span="8" 
                  :xs="24" 
                  :sm="12" 
                  :md="8"
                >
                  <el-card :body-style="{ padding: '0' }">
                    <div class="course-image">
                      <img :src="course.coverImage || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20course%20cover%20illustration&image_size=landscape_16_9'" :alt="course.title" />
                    </div>
                    <div class="course-info">
                      <h3 class="course-title">{{ course.title }}</h3>
                      <p class="course-description">{{ course.description }}</p>
                      <el-button type="primary" @click="() => { viewCourseDetail(course.id, 'collections'); playSound('点击音效2'); }" class="view-button">查看详情</el-button>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              <div v-if="!collections || collections.length === 0" class="empty-state">
                <el-empty description="暂无收藏课程" />
              </div>
              <div v-else class="pagination-container">
                <el-pagination
                  v-model:current-page="collectionsCurrentPage"
                  v-model:page-size="collectionsPageSize"
                  :page-sizes="[5, 10, 20, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="collectionsTotal"
                  @size-change="handleCollectionsSizeChange"
                  @current-change="handleCollectionsCurrentChange"
                />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="我的成就" name="achievements">
            <div class="achievements">
              <div class="achievements-header">
                <el-button type="primary" @click="backToProfile" class="back-button">
                  <el-icon><ArrowLeft /></el-icon>
                  返回个人资料
                </el-button>
              </div>
              <el-tabs v-model="activeAchievementTab" @tab-click="() => { playSound('点击音效1'); }">
                <el-tab-pane label="全部成就" name="all">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in paginatedAchievements" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card" :class="`achievement-${achievement.achievementType}`">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>
                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>

                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="!achievements || achievements.length === 0" class="empty-state">
                    <el-empty description="暂无成就" />
                  </div>
                  <div v-else class="pagination-container">
                    <el-pagination
                      v-model:current-page="achievementsCurrentPage"
                      v-model:page-size="achievementsPageSize"
                      :page-sizes="[6, 12, 24, 48]"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="achievementsTotal"
                      @size-change="handleAchievementsSizeChange"
                      @current-change="handleAchievementsCurrentChange"
                    />
                  </div>
                </el-tab-pane>
                <el-tab-pane label="学习成就" name="learning">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in getAchievementsByType('learning')" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card achievement-learning">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>

                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>
                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="getAchievementsByType('learning').length === 0" class="empty-state">
                    <el-empty description="暂无学习成就" />
                  </div>
                </el-tab-pane>
                <el-tab-pane label="时间成就" name="time">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in getAchievementsByType('time')" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card achievement-time">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>
                        <div class="achievement-header">
                          <el-tag type="warning" class="achievement-tag">{{ getAchievementTypeText(achievement.achievementType) }}</el-tag>
                        </div>
                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>
                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="getAchievementsByType('time').length === 0" class="empty-state">
                    <el-empty description="暂无时间成就" />
                  </div>
                </el-tab-pane>
                <el-tab-pane label="社交成就" name="social">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in getAchievementsByType('social')" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card achievement-social">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>
                        <div class="achievement-header">
                          <el-tag type="success" class="achievement-tag">{{ getAchievementTypeText(achievement.achievementType) }}</el-tag>
                        </div>
                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>
                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="getAchievementsByType('social').length === 0" class="empty-state">
                    <el-empty description="暂无社交成就" />
                  </div>
                </el-tab-pane>
                <el-tab-pane label="探索成就" name="explore">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in getAchievementsByType('explore')" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card achievement-explore">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>
                        <div class="achievement-header">
                          <el-tag type="info" class="achievement-tag">{{ getAchievementTypeText(achievement.achievementType) }}</el-tag>
                        </div>
                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>
                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="getAchievementsByType('explore').length === 0" class="empty-state">
                    <el-empty description="暂无探索成就" />
                  </div>
                </el-tab-pane>
                <el-tab-pane label="挑战成就" name="challenge">
                  <el-row :gutter="20">
                    <el-col 
                      v-for="achievement in getAchievementsByType('challenge')" 
                      :key="achievement.id" 
                      :span="6" 
                      :xs="12" 
                      :sm="8" 
                      :md="6"
                    >
                      <el-card class="achievement-card achievement-challenge">
                        <div class="achievement-icon">
                          <el-icon :size="48">
                            <Document v-if="achievement.achievementType === 'learning'" />
                            <Medal v-else-if="achievement.achievementType === 'time'" />
                            <Present v-else-if="achievement.achievementType === 'social' || achievement.achievementType === 'explore'" />
                            <Star v-else-if="achievement.achievementType === 'challenge'" />
                            <Star v-else />
                          </el-icon>
                        </div>
                        <div class="achievement-header">
                          <el-tag type="danger" class="achievement-tag">{{ getAchievementTypeText(achievement.achievementType) }}</el-tag>
                        </div>
                        <h3 class="achievement-title">{{ achievement.title }}</h3>
                        <p class="achievement-description">{{ achievement.description }}</p>
                        <div class="achievement-footer">
                          <p class="achievement-time">获得时间：{{ formatDate(achievement.obtainTime) }}</p>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="getAchievementsByType('challenge').length === 0" class="empty-state">
                    <el-empty description="暂无挑战成就" />
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-tab-pane>
          <el-tab-pane label="任务中心" name="tasks">
            <div class="tasks-center">
              <el-tabs v-model="activeTaskTab" @tab-click="() => { playSound('点击音效1'); }">
                <el-tab-pane label="全部任务" name="all">
                  <div class="task-list">
                    <el-card 
                      v-for="task in paginatedTasks" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag :type="getTaskTypeTagType(task.taskType)">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="!tasks || tasks.length === 0" class="empty-state">
                      <el-empty description="暂无任务" />
                    </div>
                    <div v-else class="pagination-container">
                      <el-pagination
                        v-model:current-page="tasksCurrentPage"
                        v-model:page-size="tasksPageSize"
                        :page-sizes="[5, 10, 20, 50]"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="tasksTotal"
                        @size-change="handleTasksSizeChange"
                        @current-change="handleTasksCurrentChange"
                      />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="主线任务" name="main">
                  <div class="task-list">
                    <el-card 
                      v-for="task in getTasksByType('main')" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag type="danger">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="getTasksByType('main').length === 0" class="empty-state">
                      <el-empty description="暂无主线任务" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="每日任务" name="daily">
                  <div class="task-list">
                    <div class="task-refresh" style="margin-bottom: 20px; text-align: right;">
                      <el-button type="info" @click="() => { refreshDailyTasks(); playSound('点击音效3'); }" class="refresh-button">刷新每日任务</el-button>
                    </div>
                    <el-card 
                      v-for="task in getTasksByType('daily')" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag type="primary">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="getTasksByType('daily').length === 0" class="empty-state">
                      <el-empty description="暂无每日任务" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="周常任务" name="weekly">
                  <div class="task-list">
                    <div class="task-refresh" style="margin-bottom: 20px; text-align: right;">
                      <el-button type="info" @click="() => { refreshWeeklyTasks(); playSound('点击音效3'); }" class="refresh-button">刷新周常任务</el-button>
                    </div>
                    <el-card 
                      v-for="task in getTasksByType('weekly')" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag type="warning">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="getTasksByType('weekly').length === 0" class="empty-state">
                      <el-empty description="暂无周常任务" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="挑战任务" name="challenge">
                  <div class="task-list">
                    <el-card 
                      v-for="task in getTasksByType('challenge')" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag type="success">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="getTasksByType('challenge').length === 0" class="empty-state">
                      <el-empty description="暂无挑战任务" />
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="隐藏任务" name="hidden">
                  <div class="task-list">
                    <el-card 
                      v-for="task in getTasksByType('hidden')" 
                      :key="task.id" 
                      class="task-card"
                      :class="{ 'task-completed': task.status === 'completed', 'task-claimed': task.status === 'claimed' }"
                    >
                      <div class="task-header">
                        <h3 class="task-title">{{ task.taskName }}</h3>
                        <el-tag type="info">{{ getTaskTypeText(task.taskType) }}</el-tag>
                      </div>
                      <p class="task-description">{{ task.description }}</p>
                      <div class="task-progress">
                        <el-progress :percentage="(task.progress / task.targetProgress) * 100" :color="getTaskProgressColor(task)" />
                        <span class="progress-text">{{ task.progress }}/{{ task.targetProgress }}</span>
                      </div>
                      <div class="task-reward">
                        <span class="reward-label">奖励：</span>
                        <span class="reward-value">{{ task.reward }}</span>
                      </div>
                      <div class="task-actions" v-if="task.status === 'completed'">
                        <el-button type="primary" @click="() => { claimTaskReward(task.id); playSound('点击音效2'); }" class="claim-button">领取奖励</el-button>
                      </div>
                      <div class="task-actions" v-else-if="task.status === 'claimed'">
                        <el-button type="info" disabled class="claimed-button">已领取</el-button>
                      </div>
                    </el-card>
                    <div v-if="getTasksByType('hidden').length === 0" class="empty-state">
                      <el-empty description="暂无隐藏任务" />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </el-tab-pane>
          <el-tab-pane label="积分记录" name="points">
            <div class="points-record">
              <el-table :data="paginatedPointsRecords" style="width: 100%" :row-style="{ height: '70px' }">
                <el-table-column prop="type" label="类型" min-width="120">
                  <template #default="scope">
                    <span class="points-type">
                      {{ scope.row.type }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="points" label="积分" min-width="100">
                  <template #default="scope">
                    <span class="points-value" :class="scope.row.points > 0 ? 'points-positive' : 'points-negative'">
                      {{ scope.row.points > 0 ? '+' : '' }}{{ scope.row.points }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="时间" min-width="160">
                  <template #default="scope">
                    <span class="points-time">
                      {{ scope.row.createTime }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" min-width="400">
                  <template #default="scope">
                    <span class="points-description">
                      {{ scope.row.description }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="!pointsRecord || pointsRecord.length === 0" class="empty-state">
                <el-empty description="暂无积分记录" />
              </div>
              <div v-else class="pagination-container">
                <el-pagination
                  v-model:current-page="pointsCurrentPage"
                  v-model:page-size="pointsPageSize"
                  :page-sizes="[5, 10, 20, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="pointsTotal"
                  @size-change="handlePointsSizeChange"
                  @current-change="handlePointsCurrentChange"
                />
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="我的评论" name="comments">
            <div class="my-comments">
              <el-table :data="paginatedUserComments" style="width: 100%" :row-style="{ height: '70px' }">
                <el-table-column prop="courseTitle" label="课程" min-width="200" />
                <el-table-column prop="content" label="评论内容" min-width="300">
                  <template #default="scope">
                    <span class="comments-content">
                      {{ scope.row.content }}
                    </span>
                  </template>
                </el-table-column>
                <el-table-column prop="rating" label="评分" min-width="100">
                  <template #default="scope">
                    <div class="rating-stars" @mouseleave="resetRating(scope.row)">
                      <el-icon 
                        v-for="i in 5" 
                        :key="i" 
                        :class="i <= (scope.row.tempRating || scope.row.rating) ? 'star-filled' : 'star-empty'"
                        @click="updateRating(scope.row, i)"
                        @mouseenter="previewRating(scope.row, i)"
                        style="cursor: pointer; transition: all 0.3s ease;"
                      >
                        <StarFilled v-if="i <= (scope.row.tempRating || scope.row.rating)" />
                        <Star v-else />
                      </el-icon>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="评论时间" min-width="160" />
                <el-table-column label="操作" min-width="100">
                  <template #default="scope">
                    <el-button type="primary" size="small" @click="() => { viewCourseDetail(scope.row.courseId, 'comments'); playSound('点击音效1'); }">查看课程</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="!userComments || userComments.length === 0" class="empty-state">
                <el-empty description="暂无评论" />
              </div>
              <div v-else class="pagination-container">
                <el-pagination
                  v-model:current-page="commentsCurrentPage"
                  v-model:page-size="commentsPageSize"
                  :page-sizes="[5, 10, 20, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="commentsTotal"
                  @size-change="handleCommentsSizeChange"
                  @current-change="handleCommentsCurrentChange"
                />
              </div>
            </div>
          </el-tab-pane>

        </el-tabs>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Medal, Star, StarFilled, Document, Present, ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import axios from 'axios'
import { useAudioManager } from '../utils/audioManager'

const { playSound, playClickSound, getResourceUrl } = useAudioManager()

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('profile')

// 处理用户头像，添加时间戳防止缓存
const userAvatar = computed(() => {
  const avatar = userStore.user?.avatar
  return avatar ? getResourceUrl(avatar) : getResourceUrl('/png/head1.png')
})

// 返回个人资料选项卡
const backToProfile = () => {
  activeTab.value = 'profile'
  playSound('点击音效1')
  console.log('返回个人资料选项卡')
}

// 监听选项卡切换，当切换到我的收藏选项卡时重新加载收藏数据
watch(
  () => activeTab.value,
  (newTab) => {
    if (newTab === 'collections') {
      loadCollections()
      console.log('切换到我的收藏选项卡，重新加载收藏数据')
    }
  }
)

const activeTaskTab = ref('all')
const activeAchievementTab = ref('all')
const user = ref({
  id: 1,
  username: '二次元爱好者',
  avatar: '',
  level: 5,
  experience: 1250,
  points: 580
})

// 任务数据
const tasks = ref([
  {
    id: 1,
    userId: 1,
    taskType: 'main',
    taskName: '完成第一个课程',
    description: '完成任意一个课程的学习',
    status: 'completed',
    progress: 1,
    targetProgress: 1,
    reward: '50经验值 + 20积分',
    startTime: '2024-01-01 00:00:00',
    endTime: '2024-12-31 23:59:59',
    completedTime: '2024-01-10 14:30:00'
  },
  {
    id: 2,
    userId: 1,
    taskType: 'daily',
    taskName: '每日登录',
    description: '每天登录平台',
    status: 'completed',
    progress: 1,
    targetProgress: 1,
    reward: '10经验值',
    startTime: '2024-01-15 00:00:00',
    endTime: '2024-01-15 23:59:59',
    completedTime: '2024-01-15 09:00:00'
  },
  {
    id: 3,
    userId: 1,
    taskType: 'daily',
    taskName: '观看课程',
    description: '观看10分钟课程',
    status: 'in_progress',
    progress: 5,
    targetProgress: 10,
    reward: '15经验值',
    startTime: '2024-01-15 00:00:00',
    endTime: '2024-01-15 23:59:59'
  },
  {
    id: 4,
    userId: 1,
    taskType: 'weekly',
    taskName: '周学习时长',
    description: '累计学习60分钟',
    status: 'in_progress',
    progress: 30,
    targetProgress: 60,
    reward: '50经验值',
    startTime: '2024-01-15 00:00:00',
    endTime: '2024-01-21 23:59:59'
  },
  {
    id: 5,
    userId: 1,
    taskType: 'challenge',
    taskName: '连续学习7天',
    description: '连续7天登录并学习',
    status: 'in_progress',
    progress: 3,
    targetProgress: 7,
    reward: '100经验值 + 50积分',
    startTime: '2024-01-13 00:00:00',
    endTime: '2024-01-20 23:59:59'
  },
  {
    id: 6,
    userId: 1,
    taskType: 'hidden',
    taskName: '隐藏任务',
    description: '发现并完成隐藏任务',
    status: 'pending',
    progress: 0,
    targetProgress: 1,
    reward: '神秘奖励',
    startTime: '2024-01-01 00:00:00',
    endTime: '2024-12-31 23:59:59'
  }
])

// 从本地存储获取学习进度数据
const learningProgress = ref([])
try {
  learningProgress.value = JSON.parse(localStorage.getItem('learningProgress') || '[]')
} catch (error) {
  console.error('解析学习进度数据失败:', error)
  learningProgress.value = []
}

// 学习进度分页状态
const learningProgressCurrentPage = ref(1)
const learningProgressPageSize = ref(5)
const learningProgressTotal = ref(learningProgress.value.length)

// 学习进度分页后的数据
const paginatedLearningProgress = computed(() => {
  const start = (learningProgressCurrentPage.value - 1) * learningProgressPageSize.value
  const end = start + learningProgressPageSize.value
  return learningProgress.value.slice(start, end)
})

// 我的收藏分页状态
const collections = ref([])
const collectionsCurrentPage = ref(1)
const collectionsPageSize = ref(6)
const collectionsTotal = ref(collections.value.length)

// 我的收藏分页后的数据
const paginatedCollections = computed(() => {
  const start = (collectionsCurrentPage.value - 1) * collectionsPageSize.value
  const end = start + collectionsPageSize.value
  return collections.value.slice(start, end)
})

// 我的成就数据
const achievements = ref([
  {
    id: 1,
    userId: 1,
    title: '初学者',
    description: '完成第一个课程的学习',
    achievementType: 'learning',
    icon: Star,
    obtainTime: '2026-05-09 10:00:00'
  },
  {
    id: 2,
    userId: 1,
    title: '绘画达人',
    description: '完成5个绘画课程',
    achievementType: 'learning',
    icon: Star,
    obtainTime: '2026-05-09 11:30:00'
  },
  {
    id: 3,
    userId: 1,
    title: '连续学习',
    description: '连续学习7天',
    achievementType: 'time',
    icon: Star,
    obtainTime: '2026-05-09 12:45:00'
  },
  {
    id: 4,
    userId: 1,
    title: '社交达人',
    description: '获得5个评论点赞',
    achievementType: 'social',
    icon: Star,
    obtainTime: '2026-05-09 14:00:00'
  },
  {
    id: 5,
    userId: 1,
    title: '探索者',
    description: '浏览10个不同的课程',
    achievementType: 'explore',
    icon: Star,
    obtainTime: '2026-05-09 15:30:00'
  },
  {
    id: 6,
    userId: 1,
    title: '挑战大师',
    description: '完成3个挑战任务',
    achievementType: 'challenge',
    icon: Star,
    obtainTime: '2026-05-09 16:45:00'
  }
])

// 我的成就分页状态
const achievementsCurrentPage = ref(1)
const achievementsPageSize = ref(6)
const achievementsTotal = ref(achievements.value.length)

// 我的成就分页后的数据
const paginatedAchievements = computed(() => {
  const start = (achievementsCurrentPage.value - 1) * achievementsPageSize.value
  const end = start + achievementsPageSize.value
  return achievements.value.slice(start, end)
})

// 任务中心分页状态
const tasksCurrentPage = ref(1)
const tasksPageSize = ref(5)
const tasksTotal = ref(tasks.value.length)

// 任务中心分页后的数据
const paginatedTasks = computed(() => {
  const start = (tasksCurrentPage.value - 1) * tasksPageSize.value
  const end = start + tasksPageSize.value
  return tasks.value.slice(start, end)
})

// 积分记录数据
const pointsRecord = ref([
  {
    id: 1,
    type: '学习课程',
    points: 10,
    createTime: '2024-01-15 14:30:00',
    description: '学习《二次元绘画基础》第3课时'
  },
  {
    id: 2,
    type: '发表评论',
    points: 5,
    createTime: '2024-01-14 10:15:00',
    description: '评论《动漫角色设计》课程'
  },
  {
    id: 3,
    type: '完成课程',
    points: 50,
    createTime: '2024-01-10 16:45:00',
    description: '完成《游戏原画创作》课程'
  },
  {
    id: 4,
    type: '每日登录',
    points: 2,
    createTime: '2024-01-15 09:00:00',
    description: '每日登录奖励'
  }
])

// 积分记录分页状态
const pointsCurrentPage = ref(1)
const pointsPageSize = ref(5)
const pointsTotal = ref(pointsRecord.value.length)

// 积分记录分页后的数据
const paginatedPointsRecords = computed(() => {
  const start = (pointsCurrentPage.value - 1) * pointsPageSize.value
  const end = start + pointsPageSize.value
  return pointsRecord.value.slice(start, end)
})

// 我的评论数据
const userComments = ref([])
try {
  userComments.value = JSON.parse(localStorage.getItem('userComments') || '[]')
} catch (error) {
  console.error('解析用户评论数据失败:', error)
  userComments.value = []
}

// 我的评论分页状态
const commentsCurrentPage = ref(1)
const commentsPageSize = ref(5)
const commentsTotal = ref(userComments.value.length)

// 我的评论分页后的数据
const paginatedUserComments = computed(() => {
  const start = (commentsCurrentPage.value - 1) * commentsPageSize.value
  const end = start + commentsPageSize.value
  return userComments.value.slice(start, end)
})

// 加载学习进度数据
const loadLearningProgress = () => {
  try {
    learningProgress.value = JSON.parse(localStorage.getItem('learningProgress') || '[]')
    learningProgressTotal.value = learningProgress.value.length
    console.log('加载学习进度:', learningProgress.value)
  } catch (error) {
    console.error('解析学习进度数据失败:', error)
    learningProgress.value = []
    learningProgressTotal.value = 0
  }
}

// 加载用户评论数据
const loadUserComments = () => {
  try {
    userComments.value = JSON.parse(localStorage.getItem('userComments') || '[]')
    commentsTotal.value = userComments.value.length
    console.log('加载用户评论:', userComments.value)
  } catch (error) {
    console.error('解析用户评论数据失败:', error)
    userComments.value = []
    commentsTotal.value = 0
  }
}

// 加载收藏数据
const loadCollections = () => {
  try {
    collections.value = JSON.parse(localStorage.getItem('collections') || '[]')
    collectionsTotal.value = collections.value.length
    console.log('加载收藏数据:', collections.value)
  } catch (error) {
    console.error('解析收藏数据失败:', error)
    collections.value = []
    collectionsTotal.value = 0
  }
}

// 监听本地存储变化，实时更新学习进度
window.addEventListener('storage', (event) => {
  if (event.key === 'learningProgress') {
    loadLearningProgress()
  }
  if (event.key === 'userComments') {
    loadUserComments()
  }
  if (event.key === 'collections') {
    loadCollections()
  }
})

// 学习进度分页处理函数
const handleLearningProgressSizeChange = (size) => {
  learningProgressPageSize.value = size
  learningProgressCurrentPage.value = 1
  console.log('学习进度每页显示条数:', size)
}

const handleLearningProgressCurrentChange = (current) => {
  learningProgressCurrentPage.value = current
  console.log('学习进度当前页码:', current)
}

// 我的收藏分页处理函数
const handleCollectionsSizeChange = (size) => {
  collectionsPageSize.value = size
  collectionsCurrentPage.value = 1
  console.log('我的收藏每页显示条数:', size)
}

const handleCollectionsCurrentChange = (current) => {
  collectionsCurrentPage.value = current
  console.log('我的收藏当前页码:', current)
}

// 我的成就分页处理函数
const handleAchievementsSizeChange = (size) => {
  achievementsPageSize.value = size
  achievementsCurrentPage.value = 1
  console.log('我的成就每页显示条数:', size)
}

const handleAchievementsCurrentChange = (current) => {
  achievementsCurrentPage.value = current
  console.log('我的成就当前页码:', current)
}

// 任务中心分页处理函数
const handleTasksSizeChange = (size) => {
  tasksPageSize.value = size
  tasksCurrentPage.value = 1
  console.log('任务中心每页显示条数:', size)
}

const handleTasksCurrentChange = (current) => {
  tasksCurrentPage.value = current
  console.log('任务中心当前页码:', current)
}

// 积分记录分页处理函数
const handlePointsSizeChange = (size) => {
  pointsPageSize.value = size
  pointsCurrentPage.value = 1
  console.log('积分记录每页显示条数:', size)
}

const handlePointsCurrentChange = (current) => {
  pointsCurrentPage.value = current
  console.log('积分记录当前页码:', current)
}

// 我的评论分页处理函数
const handleCommentsSizeChange = (size) => {
  commentsPageSize.value = size
  commentsCurrentPage.value = 1
  console.log('我的评论每页显示条数:', size)
}

const handleCommentsCurrentChange = (current) => {
  commentsCurrentPage.value = current
  console.log('我的评论当前页码:', current)
}

// 格式化日期，只显示年月日
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dateString.split(' ')[0]
}

// 预览评分
const previewRating = (comment, rating) => {
  comment.tempRating = rating
}

// 重置评分预览
const resetRating = (comment) => {
  delete comment.tempRating
}

// 更新评分
const updateRating = (comment, rating) => {
  comment.rating = rating
  comment.tempRating = rating
  
  // 保存更新后的评论到本地存储
  let userComments = JSON.parse(localStorage.getItem('userComments') || '[]')
  const index = userComments.findIndex(item => item.id === comment.id)
  if (index !== -1) {
    userComments[index] = comment
    localStorage.setItem('userComments', JSON.stringify(userComments))
    console.log('评论评分已更新:', comment)
  }
  
  // 播放音效
  playSound('点击音效1')
}









// 个人资料表单数据
const profileForm = ref({
  username: '',
  bio: '',
  email: '',
  gender: '',
  birthday: '',
  tags: [],
  phone: '',
  password: ''
})

const newTag = ref('')

const getProgressColor = (progress) => {
  if (progress >= 100) return '#67c23a'
  if (progress >= 60) return '#e6a23c'
  return '#f56c6c'
}

const continueLearning = (courseId) => {
  router.push(`/course/${courseId}?from=progress`)
}

const viewCourseDetail = (courseId, from = '') => {
  if (from) {
    router.push(`/course/${courseId}?from=${from}`)
  } else {
    router.push(`/course/${courseId}`)
  }
}

const goBack = () => {
  router.push('/')
}

const changeAvatar = () => {
  // 使用自定义的本地头像选项
  const avatarOptions = [
    '/png/head1.png',
    '/png/head2.png',
    '/png/head3.png',
    '/png/head4.png'
  ]

  // 创建头像选择对话框
  let dialogContent = `
    <div style="margin-bottom: 20px;">
      <h4 style="color: #40c4ff; margin-bottom: 10px;">选择预设头像</h4>
      <div style="display: flex; gap: 20px; flex-wrap: wrap;">
  `
  
  avatarOptions.forEach((avatar, index) => {
    dialogContent += `
      <div style="cursor: pointer; text-align: center;">
        <img 
          src="${avatar}" 
          alt="Avatar ${index + 1}" 
          style="width: 80px; height: 80px; border-radius: 50%; object-fit: cover; border: 2px solid #40c4ff; margin-bottom: 8px; transition: all 0.3s ease;"
          data-avatar="${avatar}"
          class="avatar-option"
        />
        <div style="font-size: 12px; color: #81d4fa;">头像 ${index + 1}</div>
      </div>
    `
  })
  
  dialogContent += `
      </div>
    </div>
    <div style="margin-bottom: 20px;">
      <h4 style="color: #40c4ff; margin-bottom: 10px;">上传本地图片</h4>
      <div style="border: 2px dashed #40c4ff; border-radius: 8px; padding: 20px; text-align: center; cursor: pointer; transition: all 0.3s ease;" onclick="document.getElementById('avatar-upload').click();">
        <input type="file" id="avatar-upload" accept="image/*" style="display: none;" />
        <div style="color: #81d4fa; font-size: 14px;">点击或拖拽文件到此处上传</div>
        <div style="color: #81d4fa; font-size: 12px; margin-top: 5px;">支持 JPG、PNG、GIF 格式，大小不超过 5MB</div>
        <div id="upload-preview" style="margin-top: 10px; display: none;">
          <img id="preview-image" src="" alt="预览" style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 2px solid #40c4ff;" />
        </div>
      </div>
    </div>
  `

  // 创建一个全局变量来存储上传的头像
  window.uploadedAvatar = null;
  window.handleFileUpload = async function(input) {
    const file = input.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        ElMessage.warning('文件大小不能超过 5MB');
        return;
      }
      
      // 显示加载状态
      const uploadArea = input.parentElement;
      const originalContent = uploadArea.innerHTML;
      uploadArea.innerHTML = '<div style="color: #40c4ff; font-size: 14px;">上传中...</div>';
      
      try {
        // 创建FormData对象
        const formData = new FormData();
        formData.append('file', file);
        
        // 上传到后端
        const response = await fetch('/api/upload/avatar', {
          method: 'POST',
          body: formData
        });
        
        const result = await response.json();
        if (result.code === 200) {
          // 上传成功，获取OSS返回的URL
          window.uploadedAvatar = result.data;
          uploadArea.innerHTML = originalContent;
          document.getElementById('upload-preview').style.display = 'block';
          document.getElementById('preview-image').src = result.data;
          // 移除其他选中状态
          document.querySelectorAll('.avatar-selected').forEach(el => el.classList.remove('avatar-selected'));
        } else {
          ElMessage.error('上传失败: ' + result.message);
          uploadArea.innerHTML = originalContent;
        }
      } catch (error) {
        ElMessage.error('上传失败: ' + error.message);
        uploadArea.innerHTML = originalContent;
      }
    }
  };
  
  // 创建对话框
  ElMessageBox.alert(dialogContent, '选择头像', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    showClose: true,
    customClass: 'avatar-select-dialog',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        // 检查是否有上传的图片
        const uploadedAvatar = window.uploadedAvatar;
        
        // 检查是否有选中的预设头像
        const selectedAvatar = document.querySelector('.avatar-selected')?.dataset.avatar;
        
        let newAvatar = null;
        if (uploadedAvatar) {
          newAvatar = uploadedAvatar;
        } else if (selectedAvatar) {
          newAvatar = selectedAvatar;
        }
        
        if (newAvatar) {
          // 更新用户头像
          user.value.avatar = newAvatar;
          // 同时更新userStore中的用户信息并持久化
          userStore.updateUserAvatar(newAvatar);
          ElMessage.success('头像更换成功！');
        }
        
        // 重置全局变量
        window.uploadedAvatar = null;
        window.handleFileUpload = null;
      }
      done();
    }
  });
  
  // 对话框打开后添加事件监听器
  setTimeout(() => {
    const fileInput = document.getElementById('avatar-upload');
    if (fileInput) {
      fileInput.addEventListener('change', function() {
        window.handleFileUpload(this);
      });
    }
    
    // 为所有头像选项添加点击事件监听器
    const avatarOptions = document.querySelectorAll('.avatar-option');
    console.log('Found avatar options:', avatarOptions.length);
    
    // 为每个头像选项添加点击事件监听器
    avatarOptions.forEach(option => {
      console.log('Adding click listener to avatar:', option.src);
      
      // 直接为现有元素添加点击事件监听器
      option.addEventListener('click', function(e) {
        e.stopPropagation();
        console.log('Avatar clicked:', this.src);
        
        // 清除所有选中状态
        avatarOptions.forEach(el => {
          el.classList.remove('avatar-selected');
          // 重置样式
          el.style.border = '2px solid #40c4ff';
          el.style.boxShadow = 'none';
          el.style.transform = 'scale(1)';
        });
        
        // 添加当前选中状态
        this.classList.add('avatar-selected');
        // 直接设置样式，确保能够生效
        this.style.border = '3px solid #ff6b9d';
        this.style.boxShadow = '0 0 15px rgba(255, 107, 157, 1)';
        this.style.transform = 'scale(1.1)';
        this.style.transition = 'all 0.3s ease';
        
        console.log('Added avatar-selected class:', this.classList.contains('avatar-selected'));
        
        // 清除上传的头像
        window.uploadedAvatar = null;
        const preview = document.getElementById('upload-preview');
        if (preview) {
          preview.style.display = 'none';
        }
      });
    });
  }, 300); // 增加延迟时间，确保对话框完全渲染
}

const saveProfile = async () => {
  // 邮箱格式验证
  if (profileForm.value.email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(profileForm.value.email)) {
      ElMessage.warning('请输入正确的邮箱格式')
      return
    }
  }
  
  // 手机号格式验证，支持带+86前缀
  if (profileForm.value.phone) {
    const phoneRegex = /^(\+86)?1[3-9]\d{9}$/
    if (!phoneRegex.test(profileForm.value.phone)) {
      ElMessage.warning('请输入正确的手机号')
      return
    }
  }
  
  try {
    // 调用后端API更新用户信息
    const token = localStorage.getItem('token')
    const response = await axios.put('/api/user/update', profileForm.value, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data.code === 200) {
      // 更新成功
      user.value.username = profileForm.value.username
      // 同时更新userStore中的用户信息
      userStore.updateUserInfo(profileForm.value)
      ElMessage.success('个人资料更新成功！')
    } else {
      // 更新失败
      ElMessage.error(response.data.message || '个人资料更新失败，请稍后重试')
    }
  } catch (error) {
    // 网络错误或其他异常
    console.error('更新个人资料错误:', error)
    if (error.response) {
      ElMessage.error(error.response.data.message || '个人资料更新失败，请稍后重试')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

const resetForm = () => {
  // 重置表单为当前用户信息
  profileForm.value = {
    username: user.value.username,
    bio: user.value.bio || '',
    email: user.value.email || '',
    gender: user.value.gender || '',
    birthday: user.value.birthday || '',
    tags: user.value.tags || [],
    phone: user.value.phone || '',
    password: ''
  }
  newTag.value = ''
}

const addTag = () => {
  if (newTag.value && !profileForm.value.tags.includes(newTag.value)) {
    profileForm.value.tags.push(newTag.value)
    newTag.value = ''
    playSound('点击音效1')
  }
}

const removeTag = (tag) => {
  const index = profileForm.value.tags.indexOf(tag)
  if (index > -1) {
    profileForm.value.tags.splice(index, 1)
  }
}

// 任务系统相关方法
const getTasksByType = (taskType) => {
  if (!tasks || !tasks.value) {
    return []
  }
  return tasks.value.filter(task => task.taskType === taskType)
}

const getTaskTypeText = (taskType) => {
  const typeMap = {
    'main': '主线任务',
    'daily': '每日任务',
    'weekly': '周常任务',
    'challenge': '挑战任务',
    'hidden': '隐藏任务'
  }
  return typeMap[taskType] || taskType
}

const getTaskTypeTagType = (taskType) => {
  const typeMap = {
    'main': 'danger',
    'daily': 'primary',
    'weekly': 'warning',
    'challenge': 'success',
    'hidden': 'info'
  }
  return typeMap[taskType] || 'info'
}

const getTaskProgressColor = (task) => {
  if (!task) {
    return '#f56c6c'
  }
  if (task.status === 'completed' || task.status === 'claimed') {
    return '#67c23a'
  } else if (task.status === 'in_progress') {
    return '#e6a23c'
  } else {
    return '#f56c6c'
  }
}

const claimTaskReward = async (taskId) => {
  try {
    // 直接在前端更新任务状态
    if (!tasks || !tasks.value) {
      return
    }
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = 'claimed'
      
      // 解析奖励并更新用户数据
      if (task.reward) {
        // 提取经验值和积分
        const experienceMatch = task.reward.match(/(\d+)经验值/)
        const pointsMatch = task.reward.match(/(\d+)积分/)
        
        let experienceGain = 0
        let pointsGain = 0
        
        if (experienceMatch) {
          experienceGain = parseInt(experienceMatch[1])
          user.value.experience += experienceGain
        }
        
        if (pointsMatch) {
          pointsGain = parseInt(pointsMatch[1])
          user.value.points += pointsGain
        }
        
        // 实现升级逻辑
        const experiencePerLevel = 20 // 每20经验升一级
        while (user.value.experience >= experiencePerLevel) {
          user.value.level += 1
          user.value.experience -= experiencePerLevel
        }
        
        console.log('奖励已领取:', {
          taskId,
          experienceGain,
          pointsGain,
          newLevel: user.value.level,
          newExperience: user.value.experience,
          newPoints: user.value.points
        })
      }
      
      ElMessage.success('奖励领取成功！')
    } else {
      ElMessage.error('任务不存在')
    }
  } catch (error) {
    console.error('领取奖励失败:', error)
    ElMessage.error('领取奖励失败，请稍后重试')
  }
}

const refreshDailyTasks = async () => {
  try {
    // 调用后端API刷新每日任务
    await axios.post(`/api/tasks/refresh/daily/${user.value.id}`)
    // 模拟刷新后的数据
    tasks.value = tasks.value.filter(task => task.taskType !== 'daily')
    // 添加新的每日任务
    const now = new Date()
    const endOfDay = new Date(now.getTime() + 24 * 60 * 60 * 1000)
    tasks.value.push(
      {
        id: Date.now() + 1,
        userId: user.value.id,
        taskType: 'daily',
        taskName: '每日登录',
        description: '每天登录平台',
        status: 'completed',
        progress: 1,
        targetProgress: 1,
        reward: '10经验值',
        startTime: now.toISOString(),
        endTime: endOfDay.toISOString(),
        completedTime: now.toISOString()
      },
      {
        id: Date.now() + 2,
        userId: user.value.id,
        taskType: 'daily',
        taskName: '观看课程',
        description: '观看10分钟课程',
        status: 'pending',
        progress: 0,
        targetProgress: 10,
        reward: '15经验值',
        startTime: now.toISOString(),
        endTime: endOfDay.toISOString()
      },
      {
        id: Date.now() + 3,
        userId: user.value.id,
        taskType: 'daily',
        taskName: '完成练习',
        description: '完成1道练习题',
        status: 'pending',
        progress: 0,
        targetProgress: 1,
        reward: '20经验值',
        startTime: now.toISOString(),
        endTime: endOfDay.toISOString()
      }
    )
    ElMessage.success('每日任务已刷新！')
  } catch (error) {
    console.error('刷新每日任务失败:', error)
    ElMessage.error('刷新每日任务失败，请稍后重试')
  }
}

const refreshWeeklyTasks = async () => {
  try {
    // 调用后端API刷新周常任务
    await axios.post(`/api/tasks/refresh/weekly/${user.value.id}`)
    // 模拟刷新后的数据
    tasks.value = tasks.value.filter(task => task.taskType !== 'weekly')
    // 添加新的周常任务
    const now = new Date()
    const endOfWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
    tasks.value.push(
      {
        id: Date.now() + 4,
        userId: user.value.id,
        taskType: 'weekly',
        taskName: '周学习时长',
        description: '累计学习60分钟',
        status: 'pending',
        progress: 0,
        targetProgress: 60,
        reward: '50经验值',
        startTime: now.toISOString(),
        endTime: endOfWeek.toISOString()
      },
      {
        id: Date.now() + 5,
        userId: user.value.id,
        taskType: 'weekly',
        taskName: '周课程数',
        description: '完成3门课程',
        status: 'pending',
        progress: 0,
        targetProgress: 3,
        reward: '60经验值',
        startTime: now.toISOString(),
        endTime: endOfWeek.toISOString()
      },
      {
        id: Date.now() + 6,
        userId: user.value.id,
        taskType: 'weekly',
        taskName: '周练习数',
        description: '完成10道练习题',
        status: 'pending',
        progress: 0,
        targetProgress: 10,
        reward: '70经验值',
        startTime: now.toISOString(),
        endTime: endOfWeek.toISOString()
      }
    )
    ElMessage.success('周常任务已刷新！')
  } catch (error) {
    console.error('刷新周常任务失败:', error)
    ElMessage.error('刷新周常任务失败，请稍后重试')
  }
}

// 成就系统相关方法
const getAchievementsByType = (achievementType) => {
  return achievements.value.filter(achievement => achievement.achievementType === achievementType)
}

const getAchievementTypeText = (achievementType) => {
  const typeMap = {
    'learning': '学习成就',
    'time': '时间成就',
    'social': '社交成就',
    'explore': '探索成就',
    'challenge': '挑战成就'
  }
  return typeMap[achievementType] || achievementType
}

const getAchievementTypeTagType = (achievementType) => {
  const typeMap = {
    'learning': 'primary',
    'time': 'warning',
    'social': 'success',
    'explore': 'info',
    'challenge': 'danger'
  }
  return typeMap[achievementType] || 'info'
}

const getAchievementIcon = (achievementType) => {
  const iconMap = {
    'learning': Document,
    'time': Medal,
    'social': Present,
    'explore': Present,
    'challenge': Star
  }
  return iconMap[achievementType] || Star
}

onMounted(() => {
  // 计算积分记录的总和
  const calculateTotalPoints = () => {
    return pointsRecord.value.reduce((total, record) => total + record.points, 0)
  }
  
  // 从用户存储中获取用户信息
  if (userStore.user) {
    user.value = userStore.user
    // 如果用户积分为0或不存在，使用积分记录的总和
    if (!user.value.points || user.value.points === 0) {
      const totalPoints = calculateTotalPoints()
      user.value.points = totalPoints
      // 更新userStore中的用户信息
      if (userStore.user) {
        userStore.user.points = totalPoints
        localStorage.setItem('user', JSON.stringify(userStore.user))
      }
    }
  } else {
    // 如果userStore中没有用户信息，使用本地初始值
    // 计算积分记录的总和并更新到user对象
    const totalPoints = calculateTotalPoints()
    user.value.points = totalPoints
  }
  
  // 初始化个人资料表单数据
  profileForm.value = {
    username: user.value.username || '二次元爱好者',
    bio: user.value.bio || '长风破浪会有时，直挂云帆济沧海！',
    email: user.value.email || '',
    gender: user.value.gender || '',
    birthday: user.value.birthday || '',
    tags: user.value.tags || ['二次元', '绘画', '动漫'],
    phone: user.value.phone || '',
    password: ''
  }
  console.log('Profile form initialized:', profileForm.value)
  console.log('Total points calculated:', user.value.points)
  
  // 加载收藏列表、学习进度和用户评论
  loadCollections()
  loadLearningProgress()
  loadUserComments()
  
  // 处理URL hash，切换到对应的标签页
  const hash = window.location.hash.substring(1) // 移除#号
  if (hash) {
    activeTab.value = hash
    console.log('切换到标签页:', hash)
  }
  
  // 动态修改性别下拉框样式
  setTimeout(() => {
    const selectWrappers = document.querySelectorAll('.el-select .el-input__wrapper')
    selectWrappers.forEach(wrapper => {
      wrapper.style.backgroundColor = '#2a2a2a'
      wrapper.style.borderRadius = '15px'
      wrapper.style.boxShadow = '0 0 8px rgba(64, 196, 255, 0.2)'
      wrapper.style.border = '1px solid #333'
      
      // 修改输入框样式
      const inputInner = wrapper.querySelector('.el-input__inner')
      if (inputInner) {
        inputInner.style.backgroundColor = '#2a2a2a'
        inputInner.style.color = '#40c4ff'
        inputInner.style.textShadow = '0 0 15px rgba(64, 196, 255, 1), 0 0 30px rgba(64, 196, 255, 0.8), 0 0 45px rgba(0, 150, 255, 0.6)'
        inputInner.style.fontWeight = 'bold'
        inputInner.style.fontSize = '16px'
        inputInner.style.border = 'none'
        inputInner.style.outline = 'none'
        inputInner.style.caretColor = '#40c4ff'
      }
    })
  }, 100)
})
</script>

<style scoped>
.user-profile {
  padding: 0;
  margin: 0;
  min-height: 100vh;
  width: 100%;
  /* 渐变背景效果 */
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a2e 33%, #16213e 66%, #0f3460 100%);
  background-size: 200% 200%;
  animation: backgroundMove 10s ease infinite;
}

/* 白色主题 */
html.light-theme .user-profile {
  background: #ffffff;
  animation: none;
}

/* 白色主题 - 移除背景图片的渐变效果 */
html.light-theme .profile-card::before {
  background: rgba(255, 255, 255, 0.7);
}

/* 白色主题 - 调整文字颜色以适应白色背景 */
html.light-theme .user-info .username {
  color: #333333;
  text-shadow: none;
}

html.light-theme .user-info .user-bio {
  color: #666666;
  text-shadow: none;
}

html.light-theme .user-stats .stat-value {
  color: #333333;
  text-shadow: none;
}

html.light-theme .user-stats .stat-label {
  color: #666666;
  text-shadow: none;
}

/* 白色主题 - 移除内容区域背景图片的渐变效果 */
html.light-theme .profile-content::before {
  background: rgba(255, 255, 255, 0.7);
}

/* 白色主题 - 调整内容区域文字颜色以适应白色背景 */
html.light-theme .profile-content :deep(.el-tabs__item) {
  color: #333333;
  text-shadow: none;
}

html.light-theme .profile-content :deep(.el-tabs__item:hover) {
  color: #000000;
  text-shadow: none;
}

html.light-theme .profile-content :deep(.el-tabs__item.is-active) {
  color: #ff6b9d;
  text-shadow: none;
}

html.light-theme .profile-content :deep(.el-form-label) {
  color: #333333;
  text-shadow: none;
}

html.light-theme .profile-content :deep(.el-input__wrapper) {
  background: white !important;
  --el-input-text-color: #333333;
}

html.light-theme .profile-content :deep(.el-select) {
  --el-select-text-color: #333333;
}

/* 个人中心标题样式 */
:deep(.el-page-header) {
  margin-bottom: 0;
  padding: 10px 20px;
}

:deep(.el-page-header__content) {
  font-size: 24px;
  font-weight: bold;
  color: #40c4ff;
  text-shadow: 0 0 8px rgba(64, 196, 255, 0.8),
               0 0 16px rgba(64, 196, 255, 0.6),
               0 0 24px rgba(64, 196, 255, 0.4);
  transition: all 0.3s ease;
  background: linear-gradient(90deg, #ff6b9d, #5f27cd, #40c4ff, #ff6b9d);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: gradientShift 3s ease infinite;
}

:deep(.el-page-header__content):hover {
  text-shadow: 0 0 15px rgba(64, 196, 255, 1),
               0 0 30px rgba(64, 196, 255, 0.8),
               0 0 45px rgba(64, 196, 255, 0.6);
  transform: scale(1.05);
}

:deep(.el-page-header__left .el-button) {
  --el-button-bg-color: rgba(64, 196, 255, 0.2);
  --el-button-border-color: #40c4ff;
  --el-button-text-color: #40c4ff;
  --el-button-hover-bg-color: rgba(64, 196, 255, 0.4);
  --el-button-hover-border-color: #81d4fa;
  --el-button-hover-text-color: #81d4fa;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.5);
  transition: all 0.3s ease;
}

:deep(.el-page-header__left .el-button:hover) {
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.8);
}

/* 渐变动画 */
@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes backgroundMove {
  0% {
    background-position: 0% 0%;
  }
  50% {
    background-position: 100% 100%;
  }
  100% {
    background-position: 0% 0%;
  }
}

.profile-card {
  margin: 0;
  padding: 0;
  background-image: url('/images/18.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border: 1px solid rgba(64, 196, 255, 0.3);
  --el-card-bg-color: transparent;
  --el-card-border-color: rgba(64, 196, 255, 0.3);
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.2);
  backdrop-filter: blur(10px);
  position: relative;
  border-radius: 0;
}

.profile-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
  border-radius: inherit;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
  z-index: 2;
  padding: 20px;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  /* 渐变边框效果 */
  border: 2px solid transparent;
  background: linear-gradient(#1a1a1a, #1a1a1a) padding-box,
              linear-gradient(45deg, #40c4ff, #ff6b9d, #9c27b0, #40c4ff) border-box;
  animation: borderRotate 4s linear infinite;
  /* 太阳发光效果 */
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.6),
              0 0 30px rgba(255, 107, 157, 0.4),
              0 0 45px rgba(156, 39, 176, 0.2);
}

.avatar::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(64, 196, 255, 0.3) 0%, rgba(255, 107, 157, 0.2) 50%, rgba(156, 39, 176, 0.1) 100%);
  border-radius: 50%;
  animation: pulse-glow 3s ease-in-out infinite;
  z-index: -1;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 0 30px rgba(64, 196, 255, 0.8),
              0 0 60px rgba(255, 107, 157, 0.6),
              0 0 90px rgba(156, 39, 176, 0.4);
}

@keyframes borderRotate {
  0% {
    background-position: 0% 0%;
  }
  100% {
    background-position: 300% 0%;
  }
}

@keyframes pulse-glow {
  0%, 100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #40c4ff;
  font-size: 14px;
  opacity: 0;
  transition: all 0.3s ease;
}

.avatar:hover .avatar-overlay {
  opacity: 1;
}

/* 性别符号样式 */
.gender-symbol {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  z-index: 10;
}

.gender-male {
  background-color: #40c4ff;
  color: white;
  font-size: 16px;
  font-weight: bold;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
  animation: pulse-male 2s infinite;
}

.gender-female {
  background-color: #ff6b9d;
  color: white;
  font-size: 16px;
  font-weight: bold;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
  animation: pulse-female 2s infinite;
}

.gender-secret {
  background-color: #9c27b0;
  color: white;
  font-size: 16px;
  font-weight: bold;
  text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
  animation: pulse-secret 2s infinite;
}

@keyframes pulse-male {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(64, 196, 255, 0.7);
  }
  70% {
    transform: scale(1.1);
    box-shadow: 0 0 0 10px rgba(64, 196, 255, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(64, 196, 255, 0);
  }
}

@keyframes pulse-female {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 107, 157, 0.7);
  }
  70% {
    transform: scale(1.1);
    box-shadow: 0 0 0 10px rgba(255, 107, 157, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 107, 157, 0);
  }
}

@keyframes pulse-secret {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(156, 39, 176, 0.7);
  }
  70% {
    transform: scale(1.1);
    box-shadow: 0 0 0 10px rgba(156, 39, 176, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(156, 39, 176, 0);
  }
}

/* 选中头像的样式 */
.avatar-selected {
  border: 3px solid #ff6b9d !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 1) !important;
  transform: scale(1.1) !important;
  transition: all 0.3s ease !important;
}

/* 确保样式能够正确应用到对话框中的头像 */
:deep(.avatar-option.avatar-selected) {
  border: 3px solid #ff6b9d !important;
  box-shadow: 0 0 15px rgba(255, 107, 157, 1) !important;
  transform: scale(1.1) !important;
  transition: all 0.3s ease !important;
}

/* 为对话框中的头像选项添加悬停效果 */
:deep(.avatar-option:hover) {
  border: 3px solid #40c4ff !important;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.8) !important;
  transform: scale(1.05) !important;
  transition: all 0.3s ease !important;
}

.user-info {
  flex: 1;
}

.user-bio {
  font-size: 12px;
  color: #81d4fa;
  text-shadow: 0 0 2px rgba(129, 212, 250, 0.8);
  margin: 6px 0 9px 0;
  line-height: 1.4;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  transition: all 0.3s ease;
}

.user-bio:hover {
  color: #40c4ff;
  text-shadow: 0 0 4px rgba(64, 196, 255, 1);
}

.username {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #ff6b9d;
  text-shadow: 0 0 5px rgba(255, 107, 157, 0.8);
}

.user-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #40c4ff;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #81d4fa;
  margin-top: 3px;
}

.profile-content {
  margin: 0;
  padding: 0;
  background: url('/images/20.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border: 1px solid rgba(255, 107, 157, 0.5);
  --el-card-bg-color: transparent;
  --el-card-border-color: rgba(255, 107, 157, 0.5);
  box-shadow: 0 0 15px rgba(255, 107, 157, 0.3);
  backdrop-filter: blur(5px);
  position: relative;
  border-radius: 0;
}

.profile-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  border-radius: inherit;
}

.profile-content > * {
  position: relative;
  z-index: 2;
}

.profile-content :deep(.el-tabs__header) {
  border-bottom: 1px solid #333;
}

.profile-content :deep(.el-tabs__item) {
  color: #9c27b0;
}

.profile-content :deep(.el-tabs__item:hover) {
  color: #ff6b9d;
}

.profile-content :deep(.el-tabs__active-bar) {
  background-color: #ff6b9d;
}

.profile-content :deep(.el-tabs__item.is-active) {
  color: #ff6b9d;
}

/* 当我的成就选项卡激活时 */
.achievements-tab-active :deep(.el-tabs__header) {
  border-bottom: none !important;
  margin-bottom: 0 !important;
  padding-bottom: 0 !important;
  height: 0 !important;
  overflow: hidden !important;
  visibility: hidden !important;
}

.achievements-tab-active :deep(.el-tabs__nav-wrap) {
  border-bottom: none !important;
  height: 0 !important;
  overflow: hidden !important;
}

.achievements-tab-active :deep(.el-tabs__nav) {
  height: 0 !important;
  overflow: hidden !important;
}

.achievements-tab-active :deep(.el-tabs__item) {
  height: 0 !important;
  overflow: hidden !important;
  visibility: hidden !important;
}

.achievements-tab-active :deep(.el-tabs__nav-wrap::after) {
  display: none !important;
}

.achievements-tab-active :deep(.el-tabs__active-bar) {
  display: none !important;
  height: 0 !important;
}

.achievements-tab-active .achievements {
  margin-top: -18px !important;
}

.achievements-tab-active .achievements :deep(.el-tabs__header) {
  margin-top: 0 !important;
  margin-bottom: 10px !important;
  height: auto !important;
  overflow: visible !important;
  visibility: visible !important;
}

.achievements-tab-active .achievements :deep(.el-tabs__nav-wrap) {
  height: auto !important;
  overflow: visible !important;
}

.achievements-tab-active .achievements :deep(.el-tabs__nav) {
  height: auto !important;
  overflow: visible !important;
}

.achievements-tab-active .achievements :deep(.el-tabs__item) {
  height: auto !important;
  overflow: visible !important;
  visibility: visible !important;
  font-size: 14px !important;
  padding: 0 20px !important;
  min-width: 60px !important;
  line-height: 40px !important;
}

/* 成就页面返回按钮样式 */
.achievements-header {
  margin-bottom: 10px;
  display: flex;
  justify-content: flex-end;
  position: relative;
}

/* 隐藏成就规则按钮 */
.achievements-header > button:not(.back-button) {
  display: none !important;
}

.back-button {
  --el-button-bg-color: rgba(64, 196, 255, 0.2);
  --el-button-border-color: #40c4ff;
  --el-button-text-color: #40c4ff;
  --el-button-hover-bg-color: rgba(64, 196, 255, 0.4);
  --el-button-hover-border-color: #81d4fa;
  --el-button-hover-text-color: #81d4fa;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.5);
  transition: all 0.3s ease;
}

.back-button:hover {
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.8);
}

.learning-progress {
  padding: 20px;
  background: rgba(10, 10, 20, 0.4);
  border-radius: 15px;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.2);
  margin-top: 20px;
  width: 100%;
}

.collections, .achievements, .points-record, .my-comments, .profile-edit {
  padding: 20px;
  background: linear-gradient(to right, rgba(10, 10, 20, 0.6)5%, rgba(10, 30, 40, 0));
  border-radius: 15px;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.2);
  margin-top: 20px;
  width: 100%;
}

.learning-progress :deep(.el-table) {
  background: #000000 !important;
  border-radius: 10px;
  overflow: hidden;
  width: 100% !important;
  --el-table-bg-color: transparent;
  --el-table-border-color: transparent;
  --el-table-text-color: #9c27b0;
}

.learning-progress :deep(.el-table th) {
  background: #1a1a1a !important;
  color: #9c27b0 !important;
  font-weight: bold !important;
  text-align: center !important;
  padding: 15px 0 !important;
  --el-table-header-bg-color: transparent;
  --el-table-header-text-color: #9c27b0;
  border-bottom-color: #333333;
}

.learning-progress :deep(.el-table td) {
  background: #000000 !important;
  color: #9c27b0 !important;
  text-align: center !important;
  vertical-align: middle !important;
  padding: 10px 0 !important;
  border-bottom-color: #333333;
}

.learning-progress :deep(.el-table tr:hover td) {
  background: #1a1a1a !important;
}

.learning-progress :deep(.el-table__row) {
  transition: all 0.3s ease !important;
}

.learning-progress :deep(.el-table__row:hover) {
  transform: scale(1.01) !important;
  box-shadow: none !important;
}

/* 积分记录和我的评论表格样式 */
.points-record :deep(.el-table),
.my-comments :deep(.el-table) {
  background: rgba(20, 20, 30, 0.8) !important;
  border-radius: 10px;
  overflow: hidden;
  width: 100% !important;
  --el-table-bg-color: transparent;
  --el-table-border-color: transparent;
  --el-table-text-color: #e0e0e0;
}

.points-record :deep(.el-table th),
.my-comments :deep(.el-table th) {
  background: rgba(30, 30, 40, 0.9) !important;
  color: #40c4ff !important;
  font-weight: bold !important;
  text-align: center !important;
  padding: 15px 0 !important;
  --el-table-header-bg-color: transparent;
  --el-table-header-text-color: #40c4ff;
  border-bottom-color: transparent;
}

.points-record :deep(.el-table td),
.my-comments :deep(.el-table td) {
  background: rgba(20, 20, 30, 0.8) !important;
  color: #e0e0e0 !important;
  text-align: center !important;
  vertical-align: middle !important;
  padding: 10px 0 !important;
  border-bottom-color: rgba(64, 196, 255, 0.1);
}

.points-record :deep(.el-table tr:hover td),
.my-comments :deep(.el-table tr:hover td) {
  background: rgba(20, 20, 30, 0.8) !important;
}

.points-record :deep(.el-table__row),
.my-comments :deep(.el-table__row) {
  transition: none !important;
}

.points-record :deep(.el-table__row:hover),
.my-comments :deep(.el-table__row:hover) {
  transform: none !important;
  box-shadow: none !important;
}

.course-title-cell {
  font-weight: bold !important;
  line-height: 1.4;
  padding: 0 10px;
  text-align: left !important;
  color: #ff4d4f !important;
}

.progress-cell {
  padding: 0 10px;
}

.time-cell {
  font-size: 12px;
  color: #52c41a !important;
  font-weight: bold !important;
}

/* 我的评价课程名颜色 */
.my-comments :deep(.el-table td:nth-child(1)) {
  color: #df0e11ff !important;
}

/* 我的评价时间颜色 */
.my-comments :deep(.el-table td:nth-child(4)) {
  color: #52c41a !important;
  font-weight: bold !important;
}

/* 积分记录时间颜色 */
.points-record :deep(.el-table td:nth-child(3)) {
  color: #52c41a !important;
  font-weight: bold !important;
}

.action-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.learning-progress :deep(.el-button--primary) {
  background: #9c27b0 !important;
  border: none !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

.learning-progress :deep(.el-button--primary:hover) {
  transform: scale(1.05) !important;
  background: #ba68c8 !important;
  box-shadow: none !important;
}

.learning-progress :deep(.el-progress__text) {
  font-size: 12px !important;
  font-weight: bold !important;
  color: #9c27b0 !important;
  text-shadow: none !important;
}

.learning-progress :deep(.el-progress__stroke) {
  box-shadow: none !important;
  background: #9c27b0 !important;
}



/* 分页控件样式 */
.pagination-container :deep(.el-pagination) {
  color: #40c4ff !important;
}

.pagination-container :deep(.el-pagination__total),
.pagination-container :deep(.el-pagination__jump),
.pagination-container :deep(.el-pagination__sizes) {
  color: #40c4ff !important;
}

.pagination-container :deep(.el-pagination__sizes .el-select .el-input__inner) {
  color: #40c4ff !important;
}

/* 星星评分样式 */
.rating-stars {
  display: flex;
  align-items: center;
  justify-content: center;
}

.star-filled {
  color: #ffd700 !important;
  text-shadow: 0 0 10px #ffd700, 0 0 20px #ffd700, 0 0 30px #ffd700;
  animation: starGlow 0.5s ease-in-out;
}

.star-empty {
  color: #ffd700 !important;
  opacity: 0.3;
}

@keyframes starGlow {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.empty-state {
  margin-top: 40px;
  text-align: center;
  color: #b0b0b0;
}

/* 返回按钮样式 */
.back-button-container {
  position: absolute;
  top: 100px;
  right: 20px;
  z-index: 100;
}

.back-to-profile-btn {
  background: linear-gradient(135deg, #40c4ff, #8a2be2);
  border: none;
  border-radius: 25px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: bold;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(64, 196, 255, 0.4);
  transition: all 0.3s ease;
}

.back-to-profile-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 196, 255, 0.6);
}

.learning-progress :deep(.el-table tr) {
  border-bottom-color: transparent;
}

.learning-progress :deep(.el-button),
.points-record :deep(.el-button),
.my-comments :deep(.el-button) {
  --el-button-bg-color: #40c4ff;
  --el-button-border-color: #40c4ff;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #81d4fa;
  --el-button-hover-border-color: #81d4fa;
  --el-button-hover-text-color: black;
  background: linear-gradient(135deg, #40c4ff, #9c27b0) !important;
  border: none !important;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.5) !important;
  transition: all 0.3s ease !important;
}

.learning-progress :deep(.el-button:hover),
.points-record :deep(.el-button:hover),
.my-comments :deep(.el-button:hover) {
  transform: scale(1.05) !important;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.8) !important;
}

.course-image {
  height: 120px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 12px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #40c4ff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-description {
  font-size: 14px;
  color: #81d4fa;
  margin-bottom: 12px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.collections :deep(.el-card) {
  background: rgba(10, 10, 20, 0.8) !important;
  border: 1px solid rgba(64, 196, 255, 0.3);
  border-radius: 10px;
  overflow: hidden;
  --el-card-bg-color: rgba(10, 10, 20, 0.8);
  --el-card-border-color: rgba(64, 196, 255, 0.3);
}

.view-button {
  width: 100%;
  --el-button-bg-color: #40c4ff;
  --el-button-border-color: #40c4ff;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #81d4fa;
  --el-button-hover-border-color: #81d4fa;
  --el-button-hover-text-color: black;
}

.achievement-card {
  text-align: center;
  padding: 20px;
  background-color: #2a2a2a;
  border: 1px solid #333;
  --el-card-bg-color: #2a2a2a;
  --el-card-border-color: #333;
}

.achievement-icon {
  margin-bottom: 12px;
  color: #e6a23c;
}

.achievement-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #40c4ff;
}

.achievement-description {
  font-size: 14px;
  color: #81d4fa;
  margin-bottom: 12px;
}

.achievement-time {
  font-size: 12px;
  color: #81d4fa;
}

.achievement-header {
  display: none !important;
}

.achievement-tag {
  font-size: 12px;
  border-radius: 10px;
  padding: 2px 8px;
}

.achievement-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
}

.achievement-time {
  font-size: 12px;
  color: #81d4fa;
  margin: 0;
  text-align: center;
}

/* 不同类型成就的样式 */
.achievement-card.achievement-learning {
  border-color: #40c4ff;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.3);
}

.achievement-card.achievement-time {
  border-color: #e6a23c;
  box-shadow: 0 0 15px rgba(230, 162, 60, 0.3);
}

.achievement-card.achievement-social {
  border-color: #67c23a;
  box-shadow: 0 0 15px rgba(103, 194, 58, 0.3);
}

.achievement-card.achievement-explore {
  border-color: #909399;
  box-shadow: 0 0 15px rgba(144, 147, 153, 0.3);
}

.achievement-card.achievement-challenge {
  border-color: #f56c6c;
  box-shadow: 0 0 15px rgba(245, 108, 108, 0.3);
}

/* 成就标签页样式 */
:deep(.el-tabs__header) {
  border-bottom: none;
  margin-bottom: 20px;
}

:deep(.el-tabs__item) {
  color: #9c27b0;
}

:deep(.el-tabs__item:hover) {
  color: #ff6b9d;
}

:deep(.el-tabs__active-bar) {
  background-color: #ff6b9d;
}

:deep(.el-tabs__item.is-active) {
  color: #ff6b9d;
}

.points-record :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-border-color: transparent;
  --el-table-text-color: #303133;
}

.points-record :deep(.el-table th) {
  --el-table-header-bg-color: transparent;
  --el-table-header-text-color: #303133;
  border-bottom-color: transparent;
}

.points-record :deep(.el-table tr) {
  border-bottom-color: transparent;
}

.points-positive {
  color: #67c23a;
}

.points-negative {
  color: #f56c6c;
}

/* 积分记录列颜色 */
.points-type {
  color: #ff6b9d !important; /* 红色 */
  font-weight: bold;
}

.points-value {
  color: #40c4ff !important; /* 青色 */
  font-weight: bold;
}

.points-time {
  color: #67c23a !important; /* 绿色 */
  font-weight: bold;
}

.points-description {
  color: #e6a23c !important; /* 金色 */
  font-weight: bold;
}

/* 评论内容颜色 */
.comments-content {
  color: #22c597ff !important; /* 青色 */
  font-weight: bold;
}

.empty-state {
  margin: 40px 0;
  text-align: center;
}

.empty-state :deep(.el-empty__description) {
  color: #81d4fa;
}

/* 个人资料编辑样式 */
.profile-edit {
  padding: 20px 0;
}

.profile-form {
  max-width: 600px;
}

.profile-form :deep(.el-form-item__label) {
  color: #ff6b9d;
  font-weight: bold;
}

.profile-form :deep(.el-input__wrapper) {
  --el-input-bg-color: rgba(255, 255, 255, 0.15);
  --el-input-border-color: rgba(64, 196, 255, 0.5);
  --el-input-text-color: #40c4ff;
  --el-input-placeholder-color: rgba(64, 196, 255, 0.7);
  border-radius: 15px;
  background-color: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(5px);
}

.profile-form :deep(.el-input__wrapper:hover) {
  --el-input-border-color: #40c4ff;
  --el-input-hover-border-color: #40c4ff;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.5);
}

.profile-form :deep(.el-input__wrapper.is-focus) {
  --el-input-border-color: #40c4ff;
  --el-input-focus-border-color: #40c4ff;
  box-shadow: 0 0 0 2px rgba(64, 196, 255, 0.4) inset, 0 0 10px rgba(64, 196, 255, 0.5);
}

.profile-form :deep(.el-input__inner) {
  font-weight: bold;
  color: #40c4ff !important;
  font-size: 16px !important;
  background-color: rgba(255, 255, 255, 0.15) !important;
  border: none !important;
  outline: none !important;
  caret-color: #40c4ff !important;
}

.profile-form :deep(.el-textarea__inner) {
  background-color: rgba(255, 255, 255, 0.15);
  border-color: rgba(64, 196, 255, 0.5);
  color: #40c4ff;
  resize: vertical;
  font-weight: bold;
  font-size: 16px !important;
  border-radius: 15px;
}

.profile-form :deep(.el-textarea__inner:focus) {
  border-color: #40c4ff;
  box-shadow: 0 0 0 2px rgba(64, 196, 255, 0.4) inset, 0 0 10px rgba(64, 196, 255, 0.5);
}

.profile-form :deep(.el-select) {
  --el-select-bg-color: rgba(255, 255, 255, 0.15);
  --el-select-border-color: rgba(64, 196, 255, 0.5);
  --el-select-text-color: #40c4ff;
  border-radius: 15px;
  background-color: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(5px);
}

.profile-form :deep(.el-select:hover) {
  --el-select-border-color: #40c4ff;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.5);
}

.profile-form :deep(.el-select.is-focus) {
  --el-select-border-color: #40c4ff;
  --el-select-focus-border-color: #40c4ff;
  box-shadow: 0 0 0 2px rgba(64, 196, 255, 0.4) inset, 0 0 10px rgba(64, 196, 255, 0.5);
}

/* 性别选择下拉菜单样式 */
.profile-form :deep(.el-select-dropdown) {
  --el-select-dropdown-bg-color: rgba(240, 248, 255, 0.9) !important;
  --el-select-dropdown-border-color: #40c4ff !important;
  --el-select-dropdown-text-color: #40c4ff !important;
  border-radius: 8px !important;
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.7) !important;
  backdrop-filter: blur(10px) !important;
  background-color: rgba(240, 248, 255, 0.9) !important;
  border: 1px solid #40c4ff !important;
}

.profile-form :deep(.el-select-dropdown__item) {
  --el-select-dropdown-item-hover-bg-color: rgba(64, 196, 255, 0.3) !important;
  --el-select-dropdown-item-text-color: #40c4ff !important;
  padding: 10px 15px !important;
  border-radius: 4px !important;
  background-color: rgba(240, 248, 255, 0.9) !important;
  color: #40c4ff !important;
  font-weight: bold !important;
}

.profile-form :deep(.el-select-dropdown__item:hover) {
  background-color: rgba(64, 196, 255, 0.3) !important;
  color: #40c4ff !important;
}

.profile-form :deep(.el-select-dropdown__item.selected) {
  --el-select-dropdown-item-selected-bg-color: rgba(64, 196, 255, 0.5) !important;
  --el-select-dropdown-item-selected-text-color: white !important;
  background-color: rgba(64, 196, 255, 0.5) !important;
  color: white !important;
  font-weight: bold !important;
}

/* 确保下拉菜单箭头颜色 */
.profile-form :deep(.el-select .el-input__suffix-inner .el-icon) {
  color: #40c4ff !important;
}

/* 确保选择框内文字颜色 */
.profile-form :deep(.el-select .el-input__inner) {
  color: #40c4ff !important;
  font-weight: bold !important;
  font-size: 16px !important;
  background-color: rgba(255, 255, 255, 0.15) !important;
}

/* 确保选择框 placeholder 颜色 */
.profile-form :deep(.el-select .el-input__inner::placeholder) {
  color: rgba(64, 196, 255, 0.7) !important;
}



.profile-form :deep(.el-date-picker) {
  --el-date-picker-bg-color: rgba(240, 248, 255, 0.9);
  --el-date-picker-border-color: #40c4ff;
  --el-date-picker-text-color: #40c4ff;
  border-radius: 15px;
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.7);
  backdrop-filter: blur(10px);
}

.profile-form :deep(.el-date-picker__header) {
  --el-date-picker-header-bg-color: rgba(240, 248, 255, 0.9);
  --el-date-picker-header-text-color: #40c4ff;
}

.profile-form :deep(.el-date-picker__body) {
  --el-date-picker-body-bg-color: rgba(240, 248, 255, 0.9);
  --el-date-picker-body-text-color: #40c4ff;
}

.profile-form :deep(.el-date-table__row:hover) {
  --el-date-table-row-hover-bg-color: rgba(64, 196, 255, 0.3);
}

.profile-form :deep(.el-date-table__cell.is-current) {
  --el-date-table-cell-current-color: #40c4ff;
}

.profile-form :deep(.el-date-table__cell.is-selected) {
  --el-date-table-cell-selected-bg-color: rgba(64, 196, 255, 0.5);
  --el-date-table-cell-selected-text-color: white;
  box-shadow: 0 0 15px rgba(64, 196, 255, 0.7);
}

/* 标签样式 */
.tag-item {
  margin-right: 10px;
  margin-bottom: 10px;
  background-color: #ff6b9d;
  color: black;
  font-weight: bold;
}

.tag-item :deep(.el-tag__close) {
  color: black;
}

.tag-item:hover {
  background-color: #ff6b9d;
}

/* 按钮样式 */
.save-button {
  --el-button-bg-color: #ff6b9d;
  --el-button-border-color: #ff6b9d;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #ff6b9d;
  --el-button-hover-border-color: #ff6b9d;
  --el-button-hover-text-color: black;
  margin-right: 10px;
  font-weight: bold;
  border-radius: 25px;
  padding: 10px 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.save-button:hover {
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: translateY(-2px) scale(1.05);
}

.reset-button {
  --el-button-bg-color: #ff6b9d;
  --el-button-border-color: #ff6b9d;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #ff6b9d;
  --el-button-hover-border-color: #ff6b9d;
  --el-button-hover-text-color: black;
  margin-right: 10px;
  font-weight: bold;
  border-radius: 25px;
  padding: 10px 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.reset-button:hover {
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: translateY(-2px) scale(1.05);
}

/* 任务中心样式 */
.tasks-center {
  padding: 20px 0;
}

.task-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.task-card {
  background-color: #2a2a2a;
  border: 1px solid #333;
  --el-card-bg-color: #2a2a2a;
  --el-card-border-color: #333;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.task-card::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(64, 196, 255, 0.1), transparent);
  transform: rotate(45deg);
  animation: shine 3s ease-in-out infinite;
  z-index: 1;
  pointer-events: none;
}

.task-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.5);
}

.task-card.task-completed {
  border-color: #67c23a;
  box-shadow: 0 0 15px rgba(103, 194, 58, 0.3);
}

.task-card.task-claimed {
  border-color: #909399;
  box-shadow: 0 0 15px rgba(144, 147, 153, 0.3);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: relative;
  z-index: 2;
}

.task-title {
  font-size: 16px;
  font-weight: bold;
  color: #40c4ff;
  margin: 0;
}

.task-description {
  font-size: 14px;
  color: #81d4fa;
  margin-bottom: 12px;
  line-height: 1.4;
  position: relative;
  z-index: 2;
}

.task-progress {
  margin-bottom: 12px;
  position: relative;
  z-index: 2;
}

.progress-text {
  font-size: 12px;
  color: #81d4fa;
  margin-top: 5px;
  display: block;
  text-align: right;
}

.task-reward {
  margin-bottom: 15px;
  position: relative;
  z-index: 2;
}

.reward-label {
  font-size: 14px;
  color: #81d4fa;
  margin-right: 5px;
}

.reward-value {
  font-size: 14px;
  font-weight: bold;
  color: #e6a23c;
}

.task-actions {
  position: relative;
  z-index: 2;
}

.claim-button {
  width: 100%;
  --el-button-bg-color: #ff6b9d;
  --el-button-border-color: #ff6b9d;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #ff6b9d;
  --el-button-hover-border-color: #ff6b9d;
  --el-button-hover-text-color: black;
  font-weight: bold;
  border-radius: 25px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(255, 107, 157, 0.3);
}

.claim-button:hover {
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.6);
  transform: scale(1.05);
}

.claimed-button {
  width: 100%;
  --el-button-bg-color: #909399;
  --el-button-border-color: #909399;
  --el-button-text-color: white;
  font-weight: bold;
  border-radius: 25px;
  padding: 8px 16px;
}

.refresh-button {
  --el-button-bg-color: #40c4ff;
  --el-button-border-color: #40c4ff;
  --el-button-text-color: black;
  --el-button-hover-bg-color: #81d4fa;
  --el-button-hover-border-color: #81d4fa;
  --el-button-hover-text-color: black;
  font-weight: bold;
  border-radius: 25px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(64, 196, 255, 0.3);
}

.refresh-button:hover {
  box-shadow: 0 0 20px rgba(64, 196, 255, 0.6);
  transform: scale(1.05);
}

/* 任务类型标签样式 */
:deep(.el-tag) {
  font-weight: bold;
  border-radius: 15px;
  padding: 2px 10px;
}

/* 任务进度条样式 */
:deep(.el-progress-bar__outer) {
  background-color: rgba(64, 196, 255, 0.2);
  border-radius: 10px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 10px;
}

/* 任务标签页样式 */
:deep(.el-tabs__header) {
  border-bottom: none;
  margin-bottom: 20px;
}

:deep(.el-tabs__item) {
  color: #9c27b0;
}

:deep(.el-tabs__item:hover) {
  color: #ff6b9d;
}

:deep(.el-tabs__active-bar) {
  background-color: #ff6b9d;
}

:deep(.el-tabs__item.is-active) {
  color: #ff6b9d;
}

/* 个人中心标题样式 */
:deep(.el-page-header__content) {
  font-size: 15px; /* 字体大小 */
  font-weight: bold; /* 字体粗细 */
  color: #30c797ff; /* 字体颜色 */
  text-shadow: 0 0 10px rgba(255, 107, 157, 0.5); /* 文字阴影 */
  transition: all 0.3s ease; /* 过渡效果 */
}

:deep(.el-page-header__content:hover) {
  transform: scale(1.05); /* 悬停时放大效果 */
  text-shadow: 0 0 20px rgba(255, 107, 157, 0.8); /* 悬停时增强阴影 */
}

</style>