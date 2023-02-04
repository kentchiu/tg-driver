import { MouseEvent } from 'react';

export declare interface CardProps {
  children?: React.ReactNode;
}

const CardRoot = (props: CardProps) => {
  return (
    <div className="flex w-full flex-col overflow-hidden rounded-sm bg-gray-800 px-0 shadow-lg">{props.children}</div>
  );
};

const Header = (props: CardProps) => {
  return <div className="flex-none p-3 ">{props.children}</div>;
};

export declare interface ContentProps {
  children?: React.ReactNode;
  onClick: (event: MouseEvent<HTMLDivElement>) => void;
}

const Content = (props: ContentProps) => {
  return (
    <div className="flex-shrink bg-gray-600" onClick={props.onClick}>
      {props.children}
    </div>
  );
};

const Footer = (props: CardProps) => {
  return <div className="flex-grow">{props.children}</div>;
};

export const Card = Object.assign(CardRoot, { Content, Footer, Header });
