//
//  SCSettingViewController.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSettingViewController.h"
#import "ProducerViewController.h"
#import "GroupListViewController.h"

@interface SCSettingViewController ()

@property (strong, nonatomic) NSArray *titles;

@end

@implementation SCSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _titles = @[@"Service Provider Setting",
                @"SamChat Contacts",
                @"SamChat Groups",
                @"User Profile",
                @"About SamChat",
                @"Log Out"];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 6;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SettingTableCellIdentifier"
                                                            forIndexPath:indexPath];
    
    cell.textLabel.text = _titles[indexPath.row];
    
    return cell;
}


// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    switch (indexPath.row) {
        case 0:
            [self pushServiceProviderSetting];
            break;
        case 1:
            [self pushSamChatContacts];
            break;
        case 2:
            [self pushSamChatGroups];
            break;
        default:
            break;
    }
}

- (void)pushServiceProviderSetting
{
    UIStoryboard *settingStoryboard = [UIStoryboard storyboardWithName:@"Setting" bundle:nil];
    ProducerViewController *producerViewController = [settingStoryboard instantiateViewControllerWithIdentifier:@"Producer"];
    [self.navigationController pushViewController:producerViewController animated:YES];
}

- (void)pushSamChatContacts
{
    ContactListViewController *contactListVC = [[ContactListViewController alloc] initWithNibName:nil bundle:nil];
    [self.navigationController pushViewController:contactListVC animated:YES];
}

- (void)pushSamChatGroups
{
    GroupListViewController *groupController = [[GroupListViewController alloc] initWithStyle:UITableViewStylePlain];
    [self.navigationController pushViewController:groupController animated:YES];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
