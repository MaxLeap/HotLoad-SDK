export { AccessKey, Account, App, CollaboratorMap, CollaboratorProperties, Deployment, DeploymentMetrics, Package, PackageInfo, UpdateMetrics } from "rest-definitions";

export interface HotLoadError {
    message?: string;
    statusCode?: number;
}

export type Headers = { [headerName: string]: string };
