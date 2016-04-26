//
//  SCHotTopicsView.m
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCHotTopicsView.h"
#import "HotTopic.h"

@interface SCHotTopicsView ()<UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) SCRefreshHeader *refreshHeaderView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) SCRefreshFooter *refreshFooterView;
@property (nonatomic, strong) NSMutableArray *hotTopics;

@property (nonatomic, assign) NSTimeInterval updateTimePre;
@property (nonatomic, assign) NSInteger currentCount;

@end

@implementation SCHotTopicsView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self _setupViews];
        [self _setupBlocks];
        self.tableView.dataSource = self;
        self.tableView.delegate = self;
    }
    return self;
}

- (void)_setupViews
{
    _titleLabel = [UILabel new];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.text = @"test";
    _titleLabel.textAlignment = NSTextAlignmentCenter;
    _titleLabel.backgroundColor = [UIColor purpleColor];
    [self addSubview:_titleLabel];
    
    _tableView = [UITableView new];
    _tableView.translatesAutoresizingMaskIntoConstraints = NO;
    _tableView.backgroundColor = [UIColor greenColor];
    [self addSubview:_tableView];
    
    _refreshHeaderView = [SCRefreshHeader refreshHeaderWithRefreshingText:@"正在刷新..."];
    _refreshHeaderView.translatesAutoresizingMaskIntoConstraints = NO;
    _refreshHeaderView.scrollView = self.tableView;
    [self addSubview:_refreshHeaderView];
    self.backgroundColor = [UIColor yellowColor];
    
    _refreshFooterView = [SCRefreshFooter refreshFooterWithRefreshingText:@"正在加载数据..."];
    [_refreshFooterView addToScrollView:self.tableView];
    
    // title label contraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_titleLabel]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[_titleLabel(44)]"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel)]];
    // tableview constraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_tableView)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[_titleLabel][_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel, _tableView)]];
}

- (void)_setupBlocks
{
    __weak typeof(self) weakSelf = self;
    _refreshHeaderView.refreshBlock = ^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf asyncLoadHotTopicsFromServerWithReset:YES];
            [weakSelf.refreshHeaderView endRefreshing];
        });
    };
    
    _refreshFooterView.refreshBlock = ^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf asyncLoadHotTopicsFromServerWithReset:NO];
            [weakSelf.refreshFooterView endRefreshing];
        });
    };
}

#pragma mark - Lazy loading
- (NSMutableArray *)hotTopics
{
    if(_hotTopics == nil){
        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        _hotTopics = [NSMutableArray arrayWithArray:[HotTopic hotTopicsWithType:0 inManagedObjectContext:mainContext]];
        [self asyncLoadHotTopicsFromServerWithReset:YES];
    }
    return _hotTopics;
}

- (void)asyncLoadHotTopicsFromServerWithReset:(BOOL)resetflag
{
    if (resetflag) {
        _updateTimePre = 0;
        _currentCount = 0;
    }
    [[SamChatClient sharedInstance] queryTopicListWithOptType:0
                                                    topicType:0
                                                 currentCount:_currentCount
                                                updateTimePre:_updateTimePre
                                                   completion:^(BOOL success, NSDictionary *response, NSError *error) {
                                                       if (success) {
                                                           NSArray *topics = [self convertJsonArrayToTopicsArray:response[SKYWORLD_TOPICS]];
                                                           _currentCount += [topics count];
                                                           _updateTimePre = [response[SKYWORLD_QUERY_TIME] doubleValue];
                                                           if (resetflag) {
                                                               [self.hotTopics removeAllObjects];
                                                           }
                                                           [self.hotTopics addObjectsFromArray:topics];
                                                           [HotTopic updateHotTopicsInPrivateManagedObjectContextWithArray:topics];
                                                           [self.tableView reloadData];
                                                       }
                                                   }];
    
//    [[SamChatClient sharedInstance] queryTopicListWithOptType:0
//                                                    topicType:0
//                                                        reset:resetflag
//                                                   completion:^(BOOL success, NSArray *topics, SCSkyWorldError *error) {
//                                                       if(success && ([topics count] > 0)){
//                                                           if(resetflag){
//                                                               [self.hotTopics removeAllObjects];
//                                                           }
//                                                           [self.hotTopics addObjectsFromArray:topics];
//                                                           [HotTopic updateHotTopicsInPrivateManagedObjectContextWithArray:topics];
//                                                           [self.tableView reloadData];
//                                                       }
//                                                   }];
}

- (NSArray *)convertJsonArrayToTopicsArray:(NSArray *)topicsJson
{
    NSMutableArray *topics = [[NSMutableArray alloc] init];
    for (id topicDictionary in topicsJson) {
        if([topicDictionary isKindOfClass:[NSDictionary class]]){
            HotTopicCellModel *topic = [[HotTopicCellModel alloc] init];
            topic.type = [((NSDictionary *)topicDictionary)[SKYWORLD_TOPIC_TYPE] integerValue];
            topic.name = ((NSDictionary *)topicDictionary)[SKYWORLD_NAME];
            if((topic.name) && (topic.name.length > 0)){
                [topics addObject:topic];
            }
        }
    }
    return topics;
}

#pragma mark - Table Data Source & Delegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.hotTopics.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;

    static NSString *HotQuestionCellIdentifier=@"HotQuestionTableCell";
    cell = [tableView dequeueReusableCellWithIdentifier:HotQuestionCellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:HotQuestionCellIdentifier];
    }
    //cell.textLabel.text = self.hotTopics[indexPath.row];
    HotTopicCellModel *topic = self.hotTopics[indexPath.row];
    cell.textLabel.text = topic.name;
    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 40;
}

//-(CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
//{
//    return 40;
//}

//- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section
//{
//    view.tintColor = [UIColor clearColor];
//}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.delegate){
        [self.delegate didSelectHotTopic:((HotTopicCellModel *)self.hotTopics[indexPath.row]).name];
    }
}

- (void)dealloc
{
    self.tableView.dataSource = nil;
    self.tableView.delegate = nil;
    [_refreshHeaderView removeFromSuperview];
}

@end
