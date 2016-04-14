//
//  SCArticlePublishViewController.m
//  SamChat
//
//  Created by HJ on 4/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticlePublishViewController.h"
#import "QBImagePickerController.h"


@interface SCArticlePublishViewController () <QBImagePickerControllerDelegate, UITextViewDelegate>

@property (nonatomic, strong) UITextView *articleCommentTextView;
@property (nonatomic, strong) UILabel *placeHolderLabel;
@property (nonatomic, strong) UIButton *addImageButton;
@property (nonatomic, strong) UIView *imagePreviewView;
@property (nonatomic, strong) NSMutableArray *imageThumbnails;
@property (nonatomic, strong) NSMutableArray *imageUrls;
//@property (nonatomic, strong) NSMutableArray *images;
@property (nonatomic, strong) QBImagePickerController *imagePickerController;


@end

@implementation SCArticlePublishViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    UIBarButtonItem *publishButton = [[UIBarButtonItem alloc] initWithTitle:@"发送"
                                                                      style:UIBarButtonItemStyleBordered
                                                                     target:self
                                                                     action:@selector(publishArticle)];
    self.navigationItem.rightBarButtonItem = publishButton;
    [self setupSubViews];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#define ARTICLE_COMMENT_HEIGHT       100
#define IMAGE_HEIGHT_WIDTH          (((SC_SCREEN_WIHTH - 5*10)/4))
#define IMAGE_PADDING               10
#define IMAGE_TAG                   1000
- (void)setupSubViews
{
    self.view.backgroundColor = [UIColor whiteColor];
    _articleCommentTextView = [[UITextView alloc] initWithFrame:CGRectMake(10, 10, SC_SCREEN_WIHTH-20, ARTICLE_COMMENT_HEIGHT)];
    _articleCommentTextView.backgroundColor = [UIColor yellowColor]; // for test
    _articleCommentTextView.font = [UIFont systemFontOfSize:15];
    _articleCommentTextView.delegate = self;
    [self.view addSubview:_articleCommentTextView];
    
    _placeHolderLabel = [[UILabel alloc] initWithFrame:CGRectMake(15, 12, SC_SCREEN_WIHTH-30, 30)];
    _placeHolderLabel.text = @"这一刻的想法...";
    _placeHolderLabel.textColor = [UIColor lightGrayColor];
    _placeHolderLabel.font = [UIFont systemFontOfSize:15];
    _placeHolderLabel.hidden = NO;
    [self.view addSubview:_placeHolderLabel];
    
    _imagePreviewView = [[UIView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(_articleCommentTextView.frame)+IMAGE_PADDING, SC_SCREEN_WIHTH, 0)];
    _imagePreviewView.backgroundColor = [UIColor yellowColor]; // for test
    [self.view addSubview:_imagePreviewView];
    
    _addImageButton = [[UIButton alloc] initWithFrame:CGRectMake(10, IMAGE_PADDING, IMAGE_HEIGHT_WIDTH, IMAGE_HEIGHT_WIDTH)];
    [_addImageButton setBackgroundImage:[UIImage imageNamed:@"addImages"] forState:UIControlStateNormal];
    [_addImageButton addTarget:self action:@selector(addImage) forControlEvents:UIControlEventTouchUpInside];
    [self.imagePreviewView  addSubview:_addImageButton];
    
    [self refreshImages];
}

#pragma mark - Lazy initialization
//- (NSMutableArray *)images
//{
//    if(_images == nil){
//        _images = [[NSMutableArray alloc] init];
//    }
//    return _images;
//}

- (NSMutableArray *)imageThumbnails
{
    if(_imageThumbnails == nil){
        _imageThumbnails = [[NSMutableArray alloc] init];
    }
    return _imageThumbnails;
}

- (NSMutableArray *)imageUrls
{
    if(_imageUrls == nil){
        _imageUrls = [[NSMutableArray alloc] init];
    }
    return _imageUrls;
}

- (QBImagePickerController *)imagePickerController
{
    if(_imagePickerController == nil){
        _imagePickerController = [[QBImagePickerController alloc] init];
        _imagePickerController.delegate = self;
        _imagePickerController.allowsMultipleSelection = YES;
        _imagePickerController.maximumNumberOfSelection = MAX_ARTICLE_IMAGE_COUNT - [self.imageThumbnails count];
        _imagePickerController.showsNumberOfSelectedAssets = YES;
        _imagePickerController.filterType = QBImagePickerControllerFilterTypePhotos;
    }
    return _imagePickerController;
}

#pragma mark - UI
- (void)refreshImages
{
    [_imagePreviewView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    NSInteger imageCount = [self.imageThumbnails count];
    CGRect imagesFrame = _imagePreviewView.frame;
    imagesFrame.size.height = ((imageCount+1)/4 + (((imageCount+1)%4)>0?1:0)) * (IMAGE_HEIGHT_WIDTH + IMAGE_PADDING) + IMAGE_PADDING;
    _imagePreviewView.frame = imagesFrame;
    for (int i=0; i<imageCount; i++) {
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(IMAGE_PADDING+(i%4)*(IMAGE_HEIGHT_WIDTH+IMAGE_PADDING),
                                                                               IMAGE_PADDING+(i/4)*(IMAGE_HEIGHT_WIDTH+IMAGE_PADDING),
                                                                               IMAGE_HEIGHT_WIDTH, IMAGE_HEIGHT_WIDTH)];
        UIButton *deleteButton = [UIButton buttonWithType:UIButtonTypeCustom];
        deleteButton.frame = CGRectMake(IMAGE_HEIGHT_WIDTH-25+5, -10, 25, 25);
        [deleteButton setImage:[UIImage imageNamed:@"deleteImage"] forState:UIControlStateNormal];
        [deleteButton addTarget:self action:@selector(deleteImage:) forControlEvents:UIControlEventTouchUpInside];
        [imageView addSubview:deleteButton];
        
        imageView.backgroundColor = [UIColor greenColor];
        //imageView.image = [UIImage imageWithCGImage:((ALAsset *)self.images[i]).thumbnail];
        imageView.image = self.imageThumbnails[i];
        imageView.userInteractionEnabled = YES;
        imageView.tag = IMAGE_TAG + i;
        [self.imagePreviewView addSubview:imageView];
    }

    CGRect addButtonFrame = _addImageButton.frame;
    addButtonFrame.origin.x = IMAGE_PADDING+(imageCount%4)*(IMAGE_HEIGHT_WIDTH+IMAGE_PADDING);
    addButtonFrame.origin.y = IMAGE_PADDING +(imageCount/4)*(IMAGE_HEIGHT_WIDTH+IMAGE_PADDING);
    _addImageButton.frame = addButtonFrame;
    [self.imagePreviewView addSubview:_addImageButton];
}

#pragma mark - Action
- (void)addImage
{
    if([self.articleCommentTextView isFirstResponder]){
        [self.articleCommentTextView resignFirstResponder];
    }
    [self presentViewController:self.imagePickerController animated:YES completion:NULL];
}

- (void)deleteImage:(UIButton *)button
{
    if([button.superview isKindOfClass:[UIImageView class]]){
        UIImageView *imageView = (UIImageView *)button.superview;
        [self.imageThumbnails removeObjectAtIndex:(imageView.tag - IMAGE_TAG)];
        [self.imageUrls removeObjectAtIndex:(imageView.tag - IMAGE_TAG)];
        //[self.images removeObjectAtIndex:(imageView.tag - IMAGE_TAG)];
        [self refreshImages];
    }
}


#pragma mark - UITextViewDelegate
- (void)textViewDidChange:(UITextView *)textView
{
    self.placeHolderLabel.hidden = [textView.text length];
}

#pragma mark - QBImagePickerControllerDelegate
- (void)qb_imagePickerController:(QBImagePickerController *)imagePickerController didSelectAsset:(ALAsset *)asset
{
    [self dismissViewControllerAnimated:YES completion:NULL];
}
- (void)qb_imagePickerController:(QBImagePickerController *)imagePickerController didSelectAssets:(NSArray *)assets
{
    //[self.images removeAllObjects];
    //[self.images addObjectsFromArray:assets];
    for (ALAsset *object in assets) {
        //[self.images addObject:[UIImage imageWithCGImage:object.defaultRepresentation.fullScreenImage]];
        [self.imageThumbnails addObject:[UIImage imageWithCGImage:object.thumbnail]];
        [self.imageUrls addObject:object.defaultRepresentation.url];
    }
    [self refreshImages];
    [self dismissViewControllerAnimated:YES completion:NULL];
    self.imagePickerController = nil;
}
- (void)qb_imagePickerControllerDidCancel:(QBImagePickerController *)imagePickerController
{
    [self dismissViewControllerAnimated:YES completion:NULL];
}

#pragma mark - Publish Article
- (void)publishArticle
{
    [self showHudInView:self.view hint:NSLocalizedString(@"article.uploading", @"Start publish article")];
    
    NSString *comment = self.articleCommentTextView.text;
    
    dispatch_queue_t queue = dispatch_queue_create("ArticleUploadQueue", DISPATCH_QUEUE_SERIAL);
    dispatch_async(queue, ^{
        ALAssetsLibrary *assetsLibrary = [[ALAssetsLibrary alloc] init];
        dispatch_semaphore_t sema = dispatch_semaphore_create(DISPATCH_TIME_NOW);
        NSMutableArray *imageArray = [[NSMutableArray alloc] init];
        __block UIImage *image = nil;
        for (NSURL *url in self.imageUrls) {
            [assetsLibrary assetForURL:url
                           resultBlock:^(ALAsset *asset) {
                               image = [UIImage imageWithCGImage:asset.defaultRepresentation.fullScreenImage];
                               image = [SCUtils scaleImage:image toMaxSize:1024];
                               DebugLog(@"image asset%@", image);
                               dispatch_semaphore_signal(sema);
                           } failureBlock:^(NSError *error) {
                               image = nil;
                               DebugLog(@"image error%@", error);
                               dispatch_semaphore_signal(sema);
                           }];
            dispatch_semaphore_wait(sema, DISPATCH_TIME_FOREVER);
            if(image){
                [imageArray addObject:image];
            }
            DebugLog(@"image array%@", imageArray);
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [[SamChatClient sharedInstance] publishArticleWithImages:imageArray
                                                             comment:comment
                                                          completion:^(BOOL success, SCSkyWorldError *error) {
                                                              [self hideHud];
                                                              if(success){
                                                                  [self showHint:@"发布成功"];
                                                                  [self.navigationController popViewControllerAnimated:YES];
                                                              }else{
                                                                  [self showHint:@"发布失败"];
                                                              }
                                                          }];
        });
    });
}

@end
