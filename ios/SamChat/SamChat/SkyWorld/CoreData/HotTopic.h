//
//  HotTopic.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "SAMCHotTopicCellModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface HotTopic : NSManagedObject

+ (NSArray *)hotTopicsWithType:(NSInteger)type inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)updateHotTopicsWithArray:(NSArray<SAMCHotTopicCellModel*> *)topics inManagedObjectContext:(NSManagedObjectContext *)context;

@end


NS_ASSUME_NONNULL_END

#import "HotTopic+CoreDataProperties.h"


