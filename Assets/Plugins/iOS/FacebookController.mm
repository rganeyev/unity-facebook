//
//  FacebookController.m
//  Unity-iPhone
//
//  Created by Roman Spiryagin on 4/13/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "FacebookController.h"

static const char *GameObjectName = "IOSLOGIN";

static const char *DidLogin = "DidLogin";
static const char *DidNotLogin = "DidNotLogin";

static const char *Empty = "";
 
@implementation FacebookController

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return (interfaceOrientation == UIInterfaceOrientationLandscapeLeft);
}


#pragma mark NSObject

- (id)initWithAppId:(NSString *)kAppId {
	self = [super init];
	if (self) {
		permissions = [[NSArray arrayWithObjects: @"offline_access",nil] retain];
        
		NSLog(@"FacebookController initWithAppId : appId = %@", kAppId);
		
		facebook = [[Facebook alloc] initWithAppId:kAppId];
		[facebook authorize:permissions delegate:self];
	}
	return self;
}

- (void)dealloc{
	[facebook release];
	[permissions release];
    
    [super dealloc];
}

#pragma mark controlling

- (void)fbDidLogin {
	NSLog(@"FB logged in");
	[facebook requestWithGraphPath:@"me" andDelegate:self];
}

-(void)fbDidNotLogin:(BOOL)cancelled {
	NSLog(@"FB did not login");
    UnitySendMessage(GameObjectName, DidNotLogin, Empty);
}

- (void)fbDidLogout {
	NSLog(@"FB did logout");
}

- (void)request:(FBRequest *)request didLoad:(id)result {
	NSString *toSend = [NSString stringWithFormat:@"%@ %@", [facebook accessToken], [result objectForKey:@"id"]];
	NSLog(@"%@", toSend);
	UnitySendMessage(GameObjectName, DidLogin, [toSend cStringUsingEncoding:NSUTF8StringEncoding]);
}

@end

static FacebookController* facebookController = nil; 

extern "C" { 
	void _fbConnect(const char *key) {
		NSLog(@"_fbConnect : %s", key);
		if(facebookController == nil) {
            facebookController = [[FacebookController alloc] initWithAppId:[NSString stringWithCString:key encoding:NSUTF8StringEncoding]];
        }
	}
	
	void _fbClose() {
		NSLog(@"_fbClose");
		if(facebookController) {
			[facebookController release];
			facebookController = nil;
		}
	}
}

