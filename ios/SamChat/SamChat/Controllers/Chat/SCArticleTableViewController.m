//
//  SCArticleTableViewController.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleTableViewController.h"

#import "SCArticleTableHeaderView.h"
#import "SCArticleRefreshHeader.h"
#import "SCArticleRefreshFooter.h"
#import "SCArticleCell.h"

#import "SCArticleCellModel.h"
#import "UITableView+SDAutoTableViewCellHeight.h"
#import "UIView+SDAutoLayout.h"
#import "SCArticlePublishViewController.h"
#import "SCArticle.h"
#define kTimeLineTableViewCellId @"SDTimeLineCell"

static CGFloat textFieldH = 40;

@interface SCArticleTableViewController () <SCArticleCellDelegate, UITextFieldDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate>

@property (nonatomic, strong) UIImagePickerController *imagePicker;

@end

@implementation SCArticleTableViewController
{
    SCArticleRefreshFooter *_refreshFooter;
    SCArticleRefreshHeader *_refreshHeader;
    CGFloat _lastScrollViewOffsetY;
    UITextField *_textField;
    CGFloat _totalKeybordHeight;
    NSIndexPath *_currentEditingIndexthPath;
    
    NSTimeInterval lastFetchTime;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    UIBarButtonItem *cameraButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCamera
                                                                                  target:self
                                                                                  action:@selector(cameraButtonClicked)];
    self.navigationItem.rightBarButtonItem = cameraButton;
    
    
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    self.edgesForExtendedLayout = UIRectEdgeTop;
    
    //[self.dataArray addObjectsFromArray:[self creatModelsWithCount:10]];
    [self fetchArticlesFromDBWithRefreshFlag:YES];
    
    __weak typeof(self) weakSelf = self;
    
    
    // 上拉加载
    _refreshFooter = [SCArticleRefreshFooter refreshFooterWithRefreshingText:@"正在加载数据..."];
//    __weak typeof(_refreshFooter) weakRefreshFooter = _refreshFooter;
    [_refreshFooter addToScrollView:self.tableView refreshOpration:^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf asyncQueryArticlesFromServerWithRefreshFlag:NO];
        });
    }];
    
    SCArticleTableHeaderView *headerView = [SCArticleTableHeaderView new];
    headerView.frame = CGRectMake(0, 0, 0, 260);
    self.tableView.tableHeaderView = headerView;
    
    [self.tableView registerClass:[SCArticleCell class] forCellReuseIdentifier:kTimeLineTableViewCellId];
    
    [self setupTextField];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardNotification:) name:UIKeyboardWillChangeFrameNotification object:nil];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (!_refreshHeader.superview) {
        
        _refreshHeader = [SCArticleRefreshHeader refreshHeaderWithCenter:CGPointMake(40, 45)];
        _refreshHeader.scrollView = self.tableView;
//        __weak typeof(_refreshHeader) weakHeader = _refreshHeader;
        __weak typeof(self) weakSelf = self;
        [_refreshHeader setRefreshingBlock:^{
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [weakSelf asyncQueryArticlesFromServerWithRefreshFlag:YES];
            });
        }];
        [self.tableView.superview addSubview:_refreshHeader];
    }
    [self asyncQueryArticlesFromServerWithRefreshFlag:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [_textField resignFirstResponder];
}

- (void)dealloc
{
    [_refreshHeader removeFromSuperview];
    [_refreshFooter removeFromSuperview];
    
    [_textField removeFromSuperview];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#define ARTICLE_FETCH_ONCE_COUNT        15
#pragma mark - Query Articles
- (void)asyncQueryArticlesFromServerWithRefreshFlag:(BOOL)refreshFlag
{
    if((refreshFlag==false) && (lastFetchTime==0)){
        [self endRefreshing];
        return; // no more articles
    }
    NSTimeInterval fromtime = lastFetchTime;
    NSTimeInterval totime = [[NSDate date] timeIntervalSince1970] * 1000; // disable inside
    if(refreshFlag){
        fromtime = [[NSDate date] timeIntervalSince1970] * 1000;
    }
    [[SamChatClient sharedInstance] queryArticleWithTimeFrom:fromtime
                                                          to:totime
                                                       count:ARTICLE_FETCH_ONCE_COUNT
                                                  completion:^(BOOL success, SCSkyWorldError *error) {
                                                      if(success){
                                                          [self fetchArticlesFromDBWithRefreshFlag:refreshFlag];
                                                      }else{
                                                          //[self showHint:@"服务器错误"];
                                                          [self endRefreshing];
                                                      }
                                                  }];
}

- (void)fetchArticlesFromDBWithRefreshFlag:(BOOL)refreshFlag
{
    if(refreshFlag){
        [self.dataArray removeAllObjects];
        lastFetchTime = [[NSDate date] timeIntervalSince1970] * 1000;
    }
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        NSArray *scarticles = [SCArticle loadArticlesEarlierThan:lastFetchTime
                                                        maxCount:ARTICLE_FETCH_ONCE_COUNT
                                          inManagedObjectContext:mainContext];
        if(scarticles){
            lastFetchTime = [((SCArticle *)[scarticles lastObject]).timestamp integerValue];
        }else{
            lastFetchTime = 0;
        }
        DebugLog(@"articles from db: %@, \nlastfetchtime:%lld", scarticles, [[NSNumber numberWithDouble:lastFetchTime] longLongValue]);
        [self.dataArray addObjectsFromArray:[self createCellModelArrayWithArticleArray:scarticles]];
    }];
    [self.tableView reloadData];
    [self endRefreshing];
}

- (void)endRefreshing
{
    [_refreshFooter endRefreshing];
    [_refreshHeader endRefreshing];
}

- (NSArray *)createCellModelArrayWithArticleArray:(NSArray *)articles
{
    NSMutableArray *cellmodelArray = [[NSMutableArray alloc] init];
    for (SCArticle *article in articles) {
        SCArticleCellModel *cellmodel = [[SCArticleCellModel alloc] initWithSCArticle:article];
        if(cellmodel){
            [cellmodelArray addObject:cellmodel];
        }
    }
    return cellmodelArray;
}


- (void)setupTextField
{
    _textField = [UITextField new];
    _textField.returnKeyType = UIReturnKeyDone;
    _textField.delegate = self;
    _textField.layer.borderColor = [[UIColor lightGrayColor] colorWithAlphaComponent:0.8].CGColor;
    _textField.layer.borderWidth = 1;
    _textField.backgroundColor = [UIColor whiteColor];
    _textField.frame = CGRectMake(0, [UIScreen mainScreen].bounds.size.height, self.view.width, textFieldH);
    [[UIApplication sharedApplication].keyWindow addSubview:_textField];
    
    [_textField becomeFirstResponder];
    [_textField resignFirstResponder];
}

- (UIImagePickerController *)imagePicker
{
    if (_imagePicker == nil) {
        _imagePicker = [[UIImagePickerController alloc] init];
        _imagePicker.modalPresentationStyle= UIModalPresentationOverFullScreen;
        _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        _imagePicker.mediaTypes = @[(NSString *)kUTTypeImage];
        _imagePicker.allowsEditing = YES;
        _imagePicker.delegate = self;
    }
    return _imagePicker;
}

#pragma mark - Action
- (void)cameraButtonClicked
{
    //[self presentViewController:self.imagePicker animated:YES completion:NULL];
    SCArticlePublishViewController *articlePublishVC = [[SCArticlePublishViewController alloc] init];
    [self.navigationController pushViewController:articlePublishVC animated:YES];
}

#pragma mark - UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{
    [self hideHud];
    UIView *mainView = [[UIApplication sharedApplication].delegate window];
    [self showHudInView:mainView hint:@"正在发布..."];
    
    UIImage *orgImage = info[UIImagePickerControllerOriginalImage];
    [picker dismissViewControllerAnimated:YES completion:nil];
    if (orgImage) {
        [[SamChatClient sharedInstance] publishArticleWithImages:@[orgImage] comment:@"comment test" completion:^(BOOL success, SCSkyWorldError *error) {
            [self hideHud];
            if(success){
                [self showHint:@"发布成功"];
            }else{
                [self showHint:@"发布失败"];
            }
        }];
    } else {
        [self hideHud];
        [self showHint:@"发布失败"];
    }
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self.imagePicker dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark table delegate and data sources
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SCArticleCell *cell = [tableView dequeueReusableCellWithIdentifier:kTimeLineTableViewCellId];
    cell.indexPath = indexPath;
    __weak typeof(self) weakSelf = self;
    if (!cell.moreButtonClickedBlock) {
        [cell setMoreButtonClickedBlock:^(NSIndexPath *indexPath) {
            SCArticleCellModel *model = weakSelf.dataArray[indexPath.row];
            model.isOpening = !model.isOpening;
            [weakSelf.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
        }];
        cell.delegate = self;
    }
    
    ////// 此步设置用于实现cell的frame缓存，可以让tableview滑动更加流畅 //////
    [cell useCellFrameCacheWithIndexPath:indexPath tableView:tableView];
    
    cell.model = self.dataArray[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    // >>>>>>>>>>>>>>>>>>>>> * cell自适应 * >>>>>>>>>>>>>>>>>>>>>>>>
    id model = self.dataArray[indexPath.row];
    return [self.tableView cellHeightForIndexPath:indexPath model:model keyPath:@"model" cellClass:[SCArticleCell class] contentViewWidth:[self cellContentViewWith]];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [_textField resignFirstResponder];
}




- (CGFloat)cellContentViewWith
{
    CGFloat width = [UIScreen mainScreen].bounds.size.width;
    
    // 适配ios7
    if ([UIApplication sharedApplication].statusBarOrientation != UIInterfaceOrientationPortrait && [[UIDevice currentDevice].systemVersion floatValue] < 8) {
        width = [UIScreen mainScreen].bounds.size.height;
    }
    return width;
}


#pragma mark - SCArticleCellDelegate

- (void)didClickcCommentButtonInCell:(SCArticleCell *)cell
{
    [_textField becomeFirstResponder];
    _currentEditingIndexthPath = [self.tableView indexPathForCell:cell];
    
    [self adjustTableViewToFitKeyboard];
    
}

- (void)didClickLickButtonInCell:(SCArticleCell *)cell
{
    _currentEditingIndexthPath = [self.tableView indexPathForCell:cell];
    [[SamChatClient sharedInstance] recommendArticleWithId:cell.model.articleId
                                                      flag:true
                                                completion:^(BOOL success, SCSkyWorldError *error) {
                                                    if(success){
                                                        DebugLog(@"recommend success");
                                                        [self reloadArticleAtIndexPath:_currentEditingIndexthPath];
                                                    }else{
                                                        DebugLog(@"recommend failed: %@", error);
                                                    }
                                                }];
}


- (void)adjustTableViewToFitKeyboard
{
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:_currentEditingIndexthPath];
    CGRect rect = [cell.superview convertRect:cell.frame toView:window];
    CGFloat delta = CGRectGetMaxY(rect) - (window.bounds.size.height - _totalKeybordHeight);
    
    CGPoint offset = self.tableView.contentOffset;
    offset.y += delta;
    if (offset.y < 0) {
        offset.y = 0;
    }
    
    [self.tableView setContentOffset:offset animated:YES];
}

- (void)reloadArticleAtIndexPath:(NSIndexPath *)indexPath
{
    SCArticleCellModel *oldCellModel = self.dataArray[indexPath.row];
    SCArticle *article = [SCArticle queryArticleWithArticleId:oldCellModel.articleId
                                       inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
    SCArticleCellModel *cellmodel = [[SCArticleCellModel alloc] initWithSCArticle:article];
    if(cellmodel){
        [self.dataArray replaceObjectAtIndex:indexPath.row withObject:cellmodel];
        [self.tableView reloadRowsAtIndexPaths:@[_currentEditingIndexthPath] withRowAnimation:UITableViewRowAnimationNone];
    }
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.text.length) {
        [_textField resignFirstResponder];
        
        SCArticleCellModel *model = self.dataArray[_currentEditingIndexthPath.row];
        
        [[SamChatClient sharedInstance] commentArticleWithId:model.articleId
                                                     comment:_textField.text
                                                  completion:^(BOOL success, SCSkyWorldError *error) {
                                                      if(success){
                                                          DebugLog(@"comment success");
                                                          [self reloadArticleAtIndexPath:_currentEditingIndexthPath];
                                                          _textField.text = @"";
                                                      }else{
                                                          DebugLog(@"comment failed:%@", error);
                                                      }
                                                  }];
        return YES;
    }
    return NO;
}



- (void)keyboardNotification:(NSNotification *)notification
{
    NSDictionary *dict = notification.userInfo;
    CGRect rect = [dict[@"UIKeyboardFrameEndUserInfoKey"] CGRectValue];
    
    
    
    CGRect textFieldRect = CGRectMake(0, rect.origin.y - textFieldH, rect.size.width, textFieldH);
    if (rect.origin.y == [UIScreen mainScreen].bounds.size.height) {
        textFieldRect = rect;
    }
    
    [UIView animateWithDuration:0.25 animations:^{
        _textField.frame = textFieldRect;
    }];
    
    CGFloat h = rect.size.height + textFieldH;
    if (_totalKeybordHeight != h) {
        _totalKeybordHeight = h;
        [self adjustTableViewToFitKeyboard];
    }
}


@end