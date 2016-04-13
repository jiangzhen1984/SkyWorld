//
//  HotTopic.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class HotTopicCellModel;
NS_ASSUME_NONNULL_BEGIN

@interface HotTopic : NSManagedObject

+ (NSArray *)hotTopicsWithType:(NSInteger)type inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)insertHotTopicsWithArray:(NSArray *)topics inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)clearEntityInManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)updateHotTopicsInPrivateManagedObjectContextWithArray:(NSArray *)topics;

@end

@interface HotTopicCellModel : NSObject

@property (nonatomic, assign) NSInteger type;
@property (nonatomic, strong) NSString *name;

@end

NS_ASSUME_NONNULL_END

#import "HotTopic+CoreDataProperties.h"


