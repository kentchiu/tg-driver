import { Switch as HLSwitch } from '@headlessui/react';

type SwitchProps = {
  toggle: boolean;
  label?: string;
  onToggle: (toggle: boolean) => void;
};

const Switch = ({ toggle, label, onToggle }: SwitchProps) => {
  return (
    <>
      <HLSwitch.Group as="div" className="flex items-center space-x-4">
        <HLSwitch.Label className="m2-4">{label}</HLSwitch.Label>
        <HLSwitch
          checked={toggle}
          onChange={onToggle}
          className={`${
            toggle ? 'bg-blue-600' : 'bg-gray-200'
          } relative inline-flex h-6 w-11 items-center rounded-full`}
        >
          <span
            className={`${
              toggle ? 'translate-x-6' : 'translate-x-1'
            } inline-block h-4 w-4 transform rounded-full bg-white`}
          />
        </HLSwitch>
      </HLSwitch.Group>
    </>
  );
};

export { Switch };
